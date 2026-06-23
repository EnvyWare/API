package com.envyful.api.neoforge.platform;

import com.envyful.api.InitializationTask;
import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.ConfigToast;
import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.neoforge.config.yaml.YamlOps;
import com.envyful.api.neoforge.player.util.UtilPlayer;
import com.envyful.api.neoforge.player.util.UtilToast;
import com.envyful.api.platform.PlatformHandler;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.platform.StandardPlatformHandler;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.scanning.PlatformScanning;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * Platform handler for the Forge platform
 *
 */
public class ForgePlatformHandler extends StandardPlatformHandler<CommandSource> {

    private static final ForgePlatformHandler INSTANCE = new ForgePlatformHandler();

    protected ForgePlatformHandler() {
        UtilConcurrency.runLater(() -> {
            ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getClasses)
                    .flatMap(Collection::stream)
                    .filter(classData -> classData.interfaces().contains(Type.getType(InitializationTask.class)))
                    .forEach(classData -> {
                        try {
                            var clazz = Class.forName(classData.clazz().getClassName());
                            var constructor = clazz.getConstructor();
                            var initializationTask = (InitializationTask) constructor.newInstance();

                            initializationTask.run();
                        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                                 IllegalAccessException | IllegalArgumentException |
                                 InvocationTargetException e) {
                            UtilLogger.getLogger().error("Error loading class", e);
                        }
                    });
        }, 20L);
    }

    public static PlatformHandler<CommandSource> getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean hasPermission(CommandSource player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        if (isOP(player)) {
            return true;
        }

        var permissionNode = UtilPlayer.getPermission(permission);

        if (permissionNode == null) {
            UtilLogger.getLogger().error("Unregistered permission node is attempted to be used: {}", permission);
            return false;
        }

        return PermissionAPI.getPermission((ServerPlayer) player, permissionNode);
    }

    @Override
    public boolean isOP(CommandSource sender) {
        if (!(sender instanceof Player)) {
            return true;
        }

        var server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            return false;
        }

        var entry = server.getPlayerList().getOps().get(((Player) sender).getGameProfile());
        return entry != null;
    }

    @Override
    public void broadcastMessage(Collection<String> message, Placeholder... placeholders) {
        List<Component> components = PlatformProxy.parse(message, placeholders);

        for (var encodedMessage : components) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastSystemMessage(encodedMessage, false);
        }
    }

    @Override
    public void sendMessage(CommandSource player, Collection<String> message, Placeholder... placeholders) {
        List<Component> components = PlatformProxy.parse(message, placeholders);

        for (var encodedMessage : components) {
            player.sendSystemMessage(encodedMessage);
        }
    }

    @Override
    public boolean isServerThread() {
        return ServerLifecycleHooks.getCurrentServer().isSameThread();
    }

    @Override
    public void runSync(Runnable runnable) {
        ServerLifecycleHooks.getCurrentServer().execute(runnable);
    }

    @Override
    public void runLater(Runnable runnable, int delayTicks) {
        ServerLifecycleHooks.getCurrentServer().tell(new TickTask(
                        ServerLifecycleHooks.getCurrentServer().getTickCount() + delayTicks,
                runnable));
    }

    @Override
    public double getTPS() {
        return ServerLifecycleHooks.getCurrentServer().getAverageTickTimeNanos();
    }

    @Override
    public void executeConsoleCommands(List<String> commands, Placeholder... placeholders) {
        if (ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer().isShutdown()) {
            return;
        }

        runSync(() -> {
            var server = ServerLifecycleHooks.getCurrentServer();
            var commandSourceStack = server.createCommandSourceStack();

            for (String command : commands) {
                for (String handlePlaceholder : PlaceholderFactory.handlePlaceholders(command, placeholders)) {
                    server.getCommands().performPrefixedCommand(commandSourceStack, handlePlaceholder);
                }
            }
        });
    }

    @Override
    public void sendToast(EnvyPlayer<CommandSource> player, ConfigToast configToast) {
        sendToast(player.getParent(), configToast);
    }

    @Override
    public void sendToast(CommandSource player, ConfigToast configToast) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        UtilToast.sendToast((ServerPlayer) player, configToast);
    }

    @Override
    public boolean isItem(Object originalItemStack, ConfigItem item) {
        if (!(originalItemStack instanceof ItemStack itemStack)) {
            return false;
        }

        if (!itemStack.getItem().builtInRegistryHolder().getRegisteredName().toString().equals(item.getType())) {
            return false;
        }

        if (item.getComponents().empty()) {
            return this.compareTag(itemStack, item);
        }

        var components = DataComponentMap.CODEC.decode(RegistryOps.create(YamlOps.INSTANCE, ServerLifecycleHooks.getCurrentServer().registryAccess()), item.getComponents()).getOrThrow().getFirst();
        var itemComponents = itemStack.getComponents();

        for (var data : components) {
            if (!itemComponents.has(data.type())) {
                return false;
            }

            var itemData = itemComponents.get(data.type());

            if (!Objects.equals(itemData, data.value())) {
                return false;
            }
        }

        return this.compareTag(itemStack, item);
    }

    private boolean compareTag(ItemStack itemStack, ConfigItem configItem) {
        if (configItem.getNbt().isEmpty()) {
            return true;
        }

        if (!itemStack.has(DataComponents.CUSTOM_DATA)) {
            return false;
        }

        var tag = itemStack.get(DataComponents.CUSTOM_DATA).getUnsafe();

        for (var entry : configItem.getNbt().entrySet()) {
            if (!tag.contains(entry.getKey())) {
                return false;
            }

            if (entry.getValue().getType().equals("string") && !tag.getString(entry.getKey()).equals(entry.getValue().getData())) {
                return false;
            }

            if (entry.getValue().getType().equals("int") && tag.getInt(entry.getKey()) != Integer.parseInt(entry.getValue().getData())) {
                return false;
            }

            if (entry.getValue().getType().equals("double") && tag.getDouble(entry.getKey()) != Double.parseDouble(entry.getValue().getData())) {
                return false;
            }

            if (entry.getValue().getType().equals("float") && tag.getFloat(entry.getKey()) != Float.parseFloat(entry.getValue().getData())) {
                return false;
            }

            if (entry.getValue().getType().equals("long") && tag.getLong(entry.getKey()) != Long.parseLong(entry.getValue().getData())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public PlatformScanning scanning() {
        return ForgePlatformScanning.INSTANCE;
    }
}
