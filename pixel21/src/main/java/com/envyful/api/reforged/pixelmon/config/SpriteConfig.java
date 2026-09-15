package com.envyful.api.reforged.pixelmon.config;

import com.envyful.api.neoforge.items.UtilItemStack;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.reforged.pixelmon.sprite.SpriteBuilder;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.PlaceholderFactory;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.Flags;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.species.gender.Gender;
import com.pixelmonmod.pixelmon.api.pokemon.species.palette.PaletteProperties;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.stats.IVStore;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.LakeTrioStats;
import com.pixelmonmod.pixelmon.api.pokemon.stats.extraStats.MewStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@ConfigSerializable
public class SpriteConfig {

    public static final SpriteConfig DEFAULT = new SpriteConfig();

    private static final Pattern PLACEHOLDER = Pattern.compile("%[a-zA-Z0-9_]+%");
    private static final Pattern FORMATTING = Pattern.compile("[&§](#[0-9a-fA-F]{6}|[0-9a-zA-Z])");

    private static final BattleStatsType[] IV_BAR_ORDER = {
            BattleStatsType.HP, BattleStatsType.ATTACK, BattleStatsType.DEFENSE,
            BattleStatsType.SPECIAL_ATTACK, BattleStatsType.SPECIAL_DEFENSE, BattleStatsType.SPEED
    };

    private String name = "&b%species_name% %nickname%";
    private String eggName = "Egg";

    private List<String> lore = Lists.newArrayList(
            "&7Lv.&f%level%%gender%%shiny%",
            "&7%type%",
            "&7%variant%",
            " ",
            "&7Nature &f%nature%%nature_effects%",
            "&7Ability &f%ability_name%%ability_ha%",
            "&7Growth &f%growth_name%",
            "&7Friendship &f%friendship%",
            "&7Held &f%held_item%",
            "%gmaxfactor%",
            "&7IVs %iv_percentage%%&7 | %ivs%",
            "    %iv_bar% &7hp atk def spa spd spe",
            "&7EVs  %ev_percentage%%&7 | %evs%",
            "%moves_line%",
            " ",
            "&7Trainer &f%original_trainer%",
            "%breedable%",
            "%untradeable%",
            "%mew_cloned%",
            "%trio_gemmed%"
    );

    private List<String> eggLore = Lists.newArrayList(
            "&aEgg Cycles: %egg_cycles%",
            "&aEgg Steps: %egg_steps%",
            "&aEgg Description: %egg_description%"
    );

    private String untradeableTrueFormat = "&cUntradeable";
    private String untradeableFalseFormat = "&aTradeable";
    private String haFormat = " &7(&c&lHA&7)";
    private String notHaFormat = "";
    private String maleFormat = " &bMale";
    private String femaleFormat = " &dFemale";
    private String noneFormat = " &fNONE";
    private String shinyTrueFormat = " &e★";
    private String shinyFalseFormat = "";
    private String breedableTrueFormat = "&aBreedable";
    private String breedableFalseFormat = "&cUnbreedable";
    private String mewClonedFormat = "&7Times Cloned: %cloned%";
    private String gemmedFormat = "&7Gemmed: %gemmed%";
    private String natureFormat = "%nature_name%%mint_nature%";
    private String mintNatureFormat = " &7(%mint_nature_name%&7)";
    private String normalIvColour = "&b";
    private String hyperIvColour = "&e";
    private String gmaxFactorTrueFormat = "&dGigantamax Factor";
    private String gmaxFactorFalseFormat = "";
    private String emptyMoveSlot = "&7Empty";
    private boolean removeEmptyMoveSlots = true;
    private boolean removeAbsentFields = true;
    private String moveSeparator = "&7, ";
    private int movesPerLine = 2;
    private String movesFormat = "&7Moves &f%moves%";
    private String movesContinuationFormat = "&7      &f%moves%";
    private String typeSeparator = "&7 / ";
    private String variantSeparator = "&7 / ";
    private String statSeparator = "&7/";
    private String natureIncreasedFormat = " &a+%stat%";
    private String natureDecreasedFormat = "&c-%stat%";
    private String ivBarGlyph = "█";
    private String ivBarPerfectColour = "&#FFD24A";
    private String ivBarHighColour = "&#6FBB7C";
    private String ivBarMediumColour = "&#6AA9FF";
    private String ivBarLowColour = "&#9A93A8";

    public SpriteConfig() {}

    public ItemStack fromPokemon(Species species, Stats form, Gender gender, PaletteProperties palette, Placeholder... additionalPlaceholders) {
        var itemStack = new SpriteBuilder().species(species).form(form).gender(gender).palette(palette.getName()).build();
        var placeholders = this.getPokemonPlaceholders(species, form, gender, palette);
        var allPlaceholders = new ArrayList<>(Arrays.asList(additionalPlaceholders));
        allPlaceholders.add(placeholders);

        List<Component> lore = PlaceholderFactory.handlePlaceholders(this.lore, PlatformProxy::parse, allPlaceholders.toArray(new Placeholder[0]));

        UtilItemStack.setLore(itemStack, lore);
        Component speciesName = PlatformProxy.flatParse(this.name, placeholders);
        UtilItemStack.setName(itemStack, speciesName.copy().withStyle(style -> style.withItalic(false)));

        return itemStack;
    }

    public ItemStack fromPokemon(Pokemon pokemon, Placeholder... additionalPlaceholders) {
        var itemStack = UtilSprite.getPixelmonSprite(pokemon);
        var placeholders = this.getPokemonPlaceholders(pokemon, additionalPlaceholders);

        UtilItemStack.setLore(itemStack, this.getLore(pokemon, placeholders));
        Component name = PlatformProxy.flatParse(pokemon.isEgg() ? this.eggName : this.name, placeholders);
        UtilItemStack.setName(itemStack, name.copy().withStyle(style -> style.withItalic(false)));

        return itemStack;
    }

    public List<Component> getLore(Pokemon pokemon) {
        var placeholders = this.getPokemonPlaceholders(pokemon);
        return this.getLore(pokemon, placeholders);
    }

    /**
     *
     * A line is dropped only when every placeholder on it rendered to nothing,
     * which is what lets an optional field either own a line or sit inline
     * beside something else. A line with no placeholders at all is a deliberate
     * spacer and always survives
     *
     */
    protected List<Component> getLore(Pokemon pokemon, Placeholder... placeholders) {
        List<Component> lore = new ArrayList<>();

        for (var line : pokemon.isEgg() ? this.eggLore : this.lore) {
            var skeleton = strip(PLACEHOLDER.matcher(line).replaceAll(""));
            var hasPlaceholder = PLACEHOLDER.matcher(line).find();

            for (var rendered : PlaceholderFactory.handlePlaceholders(line, placeholders)) {
                if (hasPlaceholder && strip(rendered).equals(skeleton)) {
                    continue;
                }

                lore.add(this.parseUnstyled(rendered));
            }
        }

        return lore;
    }

    /**
     *
     * Vanilla merges an italic style into every lore line and custom name, and
     * only fills in what we leave unset, so italic has to be explicitly false
     *
     */
    private Component parseUnstyled(String text) {
        Component parsed = PlatformProxy.parse(text);
        return parsed.copy().withStyle(style -> style.withItalic(false));
    }

    private static String strip(String text) {
        return FORMATTING.matcher(text).replaceAll("").replaceAll("\\s+", " ").trim();
    }

    public Placeholder getPokemonPlaceholders(Species species, Stats form, Gender gender, PaletteProperties palette, Placeholder... additionalPlaceholders) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(additionalPlaceholders));
        placeholders.add(Placeholder.simple("%species_name%", species.getLocalizedName()));
        placeholders.add(Placeholder.simple("%form%", form.getLocalizedName()));
        placeholders.add(Placeholder.simple("%shiny%", palette.getName().equals("shiny") ? this.shinyTrueFormat : this.shinyFalseFormat));
        placeholders.add(Placeholder.simple("%palette%", palette.getLocalizedName()));
        placeholders.add(this.getGenderPlaceholder(gender));
        return Placeholder.composition(placeholders);
    }

    public Placeholder getPokemonPlaceholders(Pokemon pokemon, Placeholder... otherPlaceholders) {
        var iVs = pokemon.getIVs();
        var ivHP = iVs.getStat(BattleStatsType.HP);
        var ivAtk = iVs.getStat(BattleStatsType.ATTACK);
        var ivDef = iVs.getStat(BattleStatsType.DEFENSE);
        var ivSpeed = iVs.getStat(BattleStatsType.SPEED);
        var ivSAtk = iVs.getStat(BattleStatsType.SPECIAL_ATTACK);
        var ivSDef = iVs.getStat(BattleStatsType.SPECIAL_DEFENSE);
        var percentage = Math.round(((ivHP + ivDef + ivAtk + ivSpeed + ivSAtk + ivSDef) / 186f) * 100);
        var evHP = pokemon.getEVs().getStat(BattleStatsType.HP);
        var evAtk = pokemon.getEVs().getStat(BattleStatsType.ATTACK);
        var evDef = pokemon.getEVs().getStat(BattleStatsType.DEFENSE);
        var evSpeed = pokemon.getEVs().getStat(BattleStatsType.SPEED);
        var evSAtk = pokemon.getEVs().getStat(BattleStatsType.SPECIAL_ATTACK);
        var evSDef = pokemon.getEVs().getStat(BattleStatsType.SPECIAL_DEFENSE);
        var extraStats = pokemon.getExtraStats();

        var hasForm = !pokemon.getForm().getName().equals(pokemon.getSpecies().getDefaultForm().getName());
        var hasPalette = !"none".equalsIgnoreCase(pokemon.getPalette().getName());
        List<String> variant = new ArrayList<>();

        if (hasForm) {
            variant.add(pokemon.getForm().getLocalizedName());
        }

        if (hasPalette) {
            variant.add(pokemon.getPalette().getLocalizedName());
        }

        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(otherPlaceholders));

        if (pokemon.isEgg()) {
            placeholders.add(Placeholder.simple("%egg_cycles%", pokemon.getEggCycles()));
            placeholders.add(Placeholder.simple("%egg_steps%", pokemon.getEggSteps()));
            placeholders.add(Placeholder.simple("%egg_description%", pokemon.getEggDescription()));
            return Placeholder.composition(placeholders);
        }

        placeholders.add(Placeholder.simple("%species_name%", pokemon.getSpecies().getLocalizedName()));
        placeholders.add(Placeholder.simple("%nickname%", pokemon.getNickname().getString()));
        placeholders.add(this.optional("%held_item%", !pokemon.getHeldItem().isEmpty(), pokemon.getHeldItem().getHoverName().getString()));
        placeholders.add(Placeholder.simple("%type%", getType(pokemon)));
        placeholders.add(this.optional("%palette%", hasPalette, pokemon.getPalette().getLocalizedName()));
        placeholders.add(Placeholder.simple("%level%", pokemon.getPokemonLevel()));
        placeholders.add(this.getGenderPlaceholder(pokemon));
        placeholders.add(this.optional("%breedable%", pokemon.hasFlag(Flags.UNBREEDABLE), !pokemon.hasFlag(Flags.UNBREEDABLE) ? this.breedableTrueFormat : this.breedableFalseFormat));
        placeholders.add(Placeholder.simple("%nature%", this.natureFormat.replace("%nature_name%",
                        pokemon.getMintNature() != null ?
                                pokemon.getBaseNature().getLocalizedName() :
                                pokemon.getNature().getLocalizedName())
                .replace("%mint_nature%", pokemon.getMintNature() != null ?
                        this.mintNatureFormat.replace("%mint_nature_name%", pokemon.getMintNature().getLocalizedName()) : "")));
        placeholders.add(Placeholder.simple("%ability_name%", pokemon.getAbility().getLocalizedName()));
        placeholders.add(Placeholder.simple("%ability_ha%", pokemon.hasHiddenAbility() ? this.haFormat : this.notHaFormat));
        placeholders.add(Placeholder.simple("%friendship%", pokemon.getFriendship()));
        placeholders.add(this.optional("%untradeable%", pokemon.isUntradeable(), pokemon.isUntradeable() ? this.untradeableTrueFormat : this.untradeableFalseFormat));
        placeholders.add(Placeholder.simple("%iv_percentage%", this.getPercentageColour(percentage) + percentage));
        placeholders.add(Placeholder.simple("%iv_hp%", getColour(iVs, BattleStatsType.HP) + ivHP));
        placeholders.add(Placeholder.simple("%iv_attack%", getColour(iVs, BattleStatsType.ATTACK) + ivAtk));
        placeholders.add(Placeholder.simple("%iv_defence%", getColour(iVs, BattleStatsType.DEFENSE) + ivDef));
        placeholders.add(Placeholder.simple("%iv_spattack%", getColour(iVs, BattleStatsType.SPECIAL_ATTACK) + ivSAtk));
        placeholders.add(Placeholder.simple("%iv_spdefence%", getColour(iVs, BattleStatsType.SPECIAL_DEFENSE) + ivSDef));
        placeholders.add(Placeholder.simple("%iv_speed%", getColour(iVs, BattleStatsType.SPEED) + ivSpeed));
        placeholders.add(Placeholder.simple("%ev_hp%", evHP));
        placeholders.add(Placeholder.simple("%ev_attack%", evAtk));
        placeholders.add(Placeholder.simple("%ev_defence%", evDef));
        placeholders.add(Placeholder.simple("%ev_spattack%", evSAtk));
        placeholders.add(Placeholder.simple("%ev_spdefence%", evSDef));
        placeholders.add(Placeholder.simple("%ev_speed%", evSpeed));
        placeholders.add(getMovePlaceholder(pokemon, 0));
        placeholders.add(getMovePlaceholder(pokemon, 1));
        placeholders.add(getMovePlaceholder(pokemon, 2));
        placeholders.add(getMovePlaceholder(pokemon, 3));
        placeholders.add(this.optional("%shiny%", pokemon.isShiny(), pokemon.isShiny() ? this.shinyTrueFormat : this.shinyFalseFormat));
        placeholders.add(this.optional("%form%", hasForm, pokemon.getForm().getLocalizedName()));
        placeholders.add(this.optional("%variant%", !variant.isEmpty(), String.join(this.variantSeparator, variant)));
        placeholders.add(Placeholder.simple("%size%", String.format("%.2f", pokemon.getSize())));
        placeholders.add(Placeholder.simple("%growth_name%", pokemon.getGrowth().value().getName().getString()));
        placeholders.add(Placeholder.simple("%iv_bar%", this.getIvBar(iVs)));
        placeholders.add(Placeholder.simple("%ivs%", this.getIvs(iVs)));
        var evTotal = evHP + evAtk + evDef + evSAtk + evSDef + evSpeed;
        placeholders.add(this.optional("%evs%", evTotal > 0, String.join(this.statSeparator,
                this.ev(evHP), this.ev(evAtk), this.ev(evDef), this.ev(evSAtk), this.ev(evSDef), this.ev(evSpeed))));
        placeholders.add(this.optional("%ev_percentage%", evTotal > 0,
                this.getPercentageColour(Math.round((evTotal / 510f) * 100)) + Math.round((evTotal / 510f) * 100)));
        placeholders.add(this.getNatureEffectsPlaceholder(pokemon));
        placeholders.add(this.getMovesLinePlaceholder(pokemon));
        placeholders.add(this.optional("%gmaxfactor%", pokemon.hasGigantamaxFactor(), pokemon.hasGigantamaxFactor() ? this.gmaxFactorTrueFormat : this.gmaxFactorFalseFormat));
        placeholders.add(
                Placeholder.require(() -> pokemon.getOriginalTrainer() != null)
                        .placeholder(Placeholder.simple("%original_trainer%", pokemon.getOriginalTrainer()))
                        .elsePlaceholder(Placeholder.simple("%original_trainer%", ""))
                        .build()
        );

        placeholders.add(
                Placeholder.require(() -> extraStats instanceof MewStats)
                        .placeholder(Placeholder.simple(s -> s
                                .replace("%mew_cloned%", this.mewClonedFormat)
                                .replace("%cloned%", ((MewStats) extraStats).numCloned + ""))
                        )
                        .elsePlaceholder(Placeholder.composition(Placeholder.empty("%mew_cloned%"), Placeholder.empty("%cloned%")))
                        .build()
        );

        placeholders.add(
                Placeholder.require(() -> extraStats instanceof LakeTrioStats)
                        .placeholder(Placeholder.simple(s -> s
                                .replace("%trio_gemmed%", this.gemmedFormat)
                                .replace("%gemmed%", ((LakeTrioStats) extraStats).numEnchanted + ""))
                        )
                        .elsePlaceholder(Placeholder.composition(Placeholder.empty("%trio_gemmed%"), Placeholder.empty("%gemmed%")))
                        .build()
        );

        return Placeholder.composition(placeholders);
    }

    public Placeholder getGenderPlaceholder(Pokemon pokemon) {
        if (pokemon == null) {
            return Placeholder.simple("%gender%", "");
        }

        return getGenderPlaceholder(pokemon.getGender());
    }

    public Placeholder getGenderPlaceholder(Gender gender) {
        if (gender == null) {
            return Placeholder.simple("%gender%", "");
        }

        if (gender == Gender.MALE) {
            return Placeholder.simple("%gender%", this.maleFormat);
        }

        if (gender == Gender.FEMALE) {
            return Placeholder.simple("%gender%", this.femaleFormat);
        }

        return this.optional("%gender%", false, this.noneFormat);
    }

    /**
     *
     * Absent values either render their "nothing here" text or drop the whole
     * lore line, depending on removeAbsentFields. Dropping is done by returning
     * a null replacement, which PlaceholderFactory treats as "delete this line"
     *
     */
    private Placeholder optional(String key, boolean present, String value) {
        if (!present && this.removeAbsentFields) {
            return Placeholder.simple(key, "");
        }

        return Placeholder.simple(key, value);
    }

    private String getIvBar(IVStore ivStore) {
        var bar = new StringBuilder();

        for (var statsType : IV_BAR_ORDER) {
            bar.append(this.getBarColour(ivStore.getStat(statsType))).append(this.ivBarGlyph);
        }

        return bar.toString();
    }

    private String getIvs(IVStore ivStore) {
        List<String> stats = new ArrayList<>();

        for (var statsType : IV_BAR_ORDER) {
            var iv = ivStore.getStat(statsType);
            stats.add(this.getBarColour(iv) + iv);
        }

        return String.join(this.statSeparator, stats);
    }

    private String ev(int ev) {
        return this.getEvColour(ev) + ev;
    }

    private String getEvColour(int ev) {
        if (ev >= 252) {
            return this.ivBarPerfectColour;
        }

        if (ev >= 128) {
            return this.ivBarHighColour;
        }

        if (ev >= 64) {
            return this.ivBarMediumColour;
        }

        return this.ivBarLowColour;
    }

    private String getPercentageColour(int percentage) {
        if (percentage >= 90) {
            return this.ivBarPerfectColour;
        }

        if (percentage >= 70) {
            return this.ivBarHighColour;
        }

        if (percentage >= 50) {
            return this.ivBarMediumColour;
        }

        return this.ivBarLowColour;
    }

    private String getBarColour(int iv) {
        if (iv >= 31) {
            return this.ivBarPerfectColour;
        }

        if (iv >= 26) {
            return this.ivBarHighColour;
        }

        if (iv >= 16) {
            return this.ivBarMediumColour;
        }

        return this.ivBarLowColour;
    }

    /**
     *
     * Neutral natures raise and lower the same stat, so they get no line rather
     * than a pair that cancels itself out. A mint overrides the original
     *
     */
    private Placeholder getNatureEffectsPlaceholder(Pokemon pokemon) {
        var nature = pokemon.getMintNature() != null ? pokemon.getMintNature() : pokemon.getNature();

        if (nature == null || nature.getIncreasedStat() == nature.getDecreasedStat()) {
            return Placeholder.simple("%nature_effects%", "");
        }

        return Placeholder.simple("%nature_effects%",
                this.natureIncreasedFormat.replace("%stat%", abbreviate(nature.getIncreasedStat()))
                        + " "
                        + this.natureDecreasedFormat.replace("%stat%", abbreviate(nature.getDecreasedStat())));
    }

    /**
     *
     * Joins the moves the Pokemon actually has, so an empty slot cannot leave a
     * dangling separator the way "%move_1%, %move_2%" in the lore would
     *
     */
    private Placeholder getMovesLinePlaceholder(Pokemon pokemon) {
        List<String> moves = new ArrayList<>();

        for (int pos = 0; pos < 4; pos++) {
            var move = this.getMove(pokemon, pos);

            if (!move.isEmpty()) {
                moves.add(move);
            }
        }

        if (moves.isEmpty()) {
            return this.optional("%moves_line%", false, "");
        }

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < moves.size(); i += this.movesPerLine) {
            var last = Math.min(i + this.movesPerLine, moves.size());
            var chunk = String.join(this.moveSeparator, moves.subList(i, last));

            if (last < moves.size()) {
                chunk = chunk + this.moveSeparator;
            }

            lines.add((i == 0 ? this.movesFormat : this.movesContinuationFormat).replace("%moves%", chunk));
        }

        return Placeholder.multiLine("%moves_line%", lines);
    }

    private static String abbreviate(BattleStatsType statsType) {
        return switch (statsType) {
            case HP -> "HP";
            case ATTACK -> "ATK";
            case DEFENSE -> "DEF";
            case SPECIAL_ATTACK -> "SPA";
            case SPECIAL_DEFENSE -> "SPD";
            case SPEED -> "SPE";
            default -> statsType.name();
        };
    }

    private String getType(Pokemon pokemon) {
        List<String> types = new ArrayList<>();

        for (var type : pokemon.getForm().getTypes()) {
            types.add(String.format("&#%06X", type.value().color().getRGB() & 0xFFFFFF) + type.value().name().getString());
        }

        return String.join(this.typeSeparator, types);
    }

    private String getColour(IVStore ivStore, BattleStatsType statsType) {
        if (ivStore.isHyperTrained(statsType)) {
            return this.hyperIvColour;
        }

        return this.normalIvColour;
    }

    private String getMove(Pokemon pokemon, int pos) {
        if (pokemon.getMoveset() == null) {
            return "";
        }

        if (pokemon.getMoveset().attacks.length <= pos) {
            return "";
        }

        if (pokemon.getMoveset().attacks[pos] == null) {
            return "";
        }

        return pokemon.getMoveset().attacks[pos].getActualMove().getLocalizedName();
    }

    private Placeholder getMovePlaceholder(Pokemon pokemon, int pos) {
        return Placeholder.require(() -> {
                    if (pokemon.getMoveset() == null) {
                        return false;
                    }

                    if (pokemon.getMoveset().attacks.length <= pos) {
                        return false;
                    }

                    return pokemon.getMoveset().attacks[pos] != null;
                }).placeholder(Placeholder.simple("%move_" + (pos + 1) + "%", getMove(pokemon, pos)))
                .elsePlaceholder(this.removeEmptyMoveSlots ? Placeholder.empty("%move_" + (pos + 1) + "%") : Placeholder.simple("%move_" + (pos + 1) + "%", this.emptyMoveSlot))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SpriteConfig config = new SpriteConfig();

        public Builder name(String name) {
            this.config.name = name;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.config.lore = lore;
            return this;
        }

        public Builder lore(String... lore) {
            this.config.lore = Lists.newArrayList(lore);
            return this;
        }

        public Builder addLore(String... lore) {
            this.config.lore.addAll(List.of(lore));
            return this;
        }

        public Builder untrdeableTrueFormat(String untrdeableTrueFormat) {
            this.config.untradeableTrueFormat = untrdeableTrueFormat;
            return this;
        }

        public Builder untradeableFalseFormat(String untradeableFalseFormat) {
            this.config.untradeableFalseFormat = untradeableFalseFormat;
            return this;
        }

        public Builder haFormat(String haFormat) {
            this.config.haFormat = haFormat;
            return this;
        }

        public Builder maleFormat(String maleFormat) {
            this.config.maleFormat = maleFormat;
            return this;
        }

        public Builder femaleFormat(String femaleFormat) {
            this.config.femaleFormat = femaleFormat;
            return this;
        }

        public Builder noneFormat(String noneFormat) {
            this.config.noneFormat = noneFormat;
            return this;
        }

        public Builder shinyTrueFormat(String shinyTrueFormat) {
            this.config.shinyTrueFormat = shinyTrueFormat;
            return this;
        }

        public Builder shinyFalseFormat(String shinyFalseFormat) {
            this.config.shinyFalseFormat = shinyFalseFormat;
            return this;
        }

        public Builder unbreedableTrueFormat(String unbreedableTrueFormat) {
            this.config.breedableTrueFormat = unbreedableTrueFormat;
            return this;
        }

        public Builder unbreedableFalseFormat(String unbreedableFalseFormat) {
            this.config.breedableFalseFormat = unbreedableFalseFormat;
            return this;
        }

        public Builder mewClonedFormat(String mewClonedFormat) {
            this.config.mewClonedFormat = mewClonedFormat;
            return this;
        }

        public Builder gemmedFormat(String gemmedFormat) {
            this.config.gemmedFormat = gemmedFormat;
            return this;
        }

        public Builder natureFormat(String natureFormat) {
            this.config.natureFormat = natureFormat;
            return this;
        }

        public Builder mintNatureFormat(String mintNatureFormat) {
            this.config.mintNatureFormat = mintNatureFormat;
            return this;
        }

        public Builder normalIvColour(String normalIvColour) {
            this.config.normalIvColour = normalIvColour;
            return this;
        }

        public Builder hyperIvColour(String hyperIvColour) {
            this.config.hyperIvColour = hyperIvColour;
            return this;
        }

        public Builder gmaxFactorTrueFormat(String gmaxFactorTrueFormat) {
            this.config.gmaxFactorTrueFormat = gmaxFactorTrueFormat;
            return this;
        }

        public Builder gmaxFactorFalseFormat(String gmaxFactorFalseFormat) {
            this.config.gmaxFactorFalseFormat = gmaxFactorFalseFormat;
            return this;
        }

        public Builder removeAbsentFields(boolean removeAbsentFields) {
            this.config.removeAbsentFields = removeAbsentFields;
            return this;
        }

        public Builder moveSeparator(String moveSeparator) {
            this.config.moveSeparator = moveSeparator;
            return this;
        }

        public Builder natureIncreasedFormat(String natureIncreasedFormat) {
            this.config.natureIncreasedFormat = natureIncreasedFormat;
            return this;
        }

        public Builder natureDecreasedFormat(String natureDecreasedFormat) {
            this.config.natureDecreasedFormat = natureDecreasedFormat;
            return this;
        }

        public Builder typeSeparator(String typeSeparator) {
            this.config.typeSeparator = typeSeparator;
            return this;
        }

        public Builder variantSeparator(String variantSeparator) {
            this.config.variantSeparator = variantSeparator;
            return this;
        }

        public Builder statSeparator(String statSeparator) {
            this.config.statSeparator = statSeparator;
            return this;
        }

        public Builder movesPerLine(int movesPerLine) {
            this.config.movesPerLine = movesPerLine;
            return this;
        }

        public Builder movesFormat(String movesFormat, String movesContinuationFormat) {
            this.config.movesFormat = movesFormat;
            this.config.movesContinuationFormat = movesContinuationFormat;
            return this;
        }

        public Builder ivBarGlyph(String ivBarGlyph) {
            this.config.ivBarGlyph = ivBarGlyph;
            return this;
        }

        public Builder ivBarColours(String perfect, String high, String medium, String low) {
            this.config.ivBarPerfectColour = perfect;
            this.config.ivBarHighColour = high;
            this.config.ivBarMediumColour = medium;
            this.config.ivBarLowColour = low;
            return this;
        }

        public SpriteConfig build() {
            return this.config;
        }
    }
}
