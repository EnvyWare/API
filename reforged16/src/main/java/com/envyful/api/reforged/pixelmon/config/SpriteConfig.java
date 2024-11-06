package com.envyful.api.reforged.pixelmon.config;

import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class SpriteConfig {

    public static final transient SpriteConfig DEFAULT = new SpriteConfig();

    private String name = "&b%species_name% %nickname%";

    private List<String> lore = Lists.newArrayList(
            "&7Level: &b%level%",
            "&7Shiny: &b%shiny%",
            "&7Palette: &b%palette%",
            "&7Gender: %gender%",
            "&7Breedable: %breedable%",
            "&7Friendship: %friendship%",
            "&7Nature: &b%nature%",
            "&7Form: &b%form%",
            "&7Growth: &b%size%",
            "&7Ability: &b%ability%",
            "&7Friendship: &b%friendship%",
            "&7Untradeable: &b%untradeable%",
            " ",
            "&7IVs (&b%iv_percentage%%&7):",
            "    §7HP: %iv_hp% §d| §7Atk: %iv_attack% §d| §7Def: %iv_defence%",
            "    §7SAtk: %iv_spattack% §d| §7SDef: %iv_spdefence% §d| §7Spd: %iv_speed%",
            " ",
            "&7EVs:",
            "    §7HP: &b%ev_hp% §d| §7Atk: &b%ev_attack% §d| §7Def: &b%ev_defence%",
            "    §7SAtk: &b%ev_spattack% §d| §7SDef: &b%ev_spdefence% §d| §7Spd: &b%ev_speed%",
            " ",
            "&7Moves:",
            "    &b%move_1%",
            "    &b%move_2%",
            "    &b%move_3%",
            "    &b%move_4%",
            " ",
            "%mew_cloned%",
            "%trio_gemmed%"
    );

    private String untrdeableTrueFormat = "&aTRUE";
    private String untradeableFalseFormat = "&cFALSE";
    private String abilityFormat = "%ability_name% %ability_ha%";
    private String haFormat = "&7(&c&lHA&7)";
    private String maleFormat = "&bMale";
    private String femaleFormat = "&dFemale";
    private String noneFormat = "&fNONE";
    private String shinyTrueFormat = "&aTRUE";
    private String shinyFalseFormat = "&cFALSE";
    private String unbreedableTrueFormat = "&aTRUE";
    private String unbreedableFalseFormat = "&cFALSE";
    private String mewClonedFormat = "&7Times Cloned: %cloned%";
    private String gemmedFormat = "&7Gemmed: %gemmed%";
    private String natureFormat = "%nature_name% %mint_nature%";
    private String mintNatureFormat = "&7(%mint_nature_name%&7)";
    private String normalIvColour = "&b";
    private String hyperIvColour = "&e";
    private String notApplicable = "&7N/A";

    public SpriteConfig() {}

    public String getHaFormat() {
        return this.haFormat;
    }

    public String getAbilityFormat() {
        return this.abilityFormat;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public String getUntrdeableTrueFormat() {
        return this.untrdeableTrueFormat;
    }

    public String getUntradeableFalseFormat() {
        return this.untradeableFalseFormat;
    }

    public String getMaleFormat() {
        return this.maleFormat;
    }

    public String getFemaleFormat() {
        return this.femaleFormat;
    }

    public String getUnbreedableTrueFormat() {
        return this.unbreedableTrueFormat;
    }

    public String getUnbreedableFalseFormat() {
        return this.unbreedableFalseFormat;
    }

    public String getNoneFormat() {
        return this.noneFormat;
    }

    public String getMewClonedFormat() {
        return this.mewClonedFormat;
    }

    public String getGemmedFormat() {
        return this.gemmedFormat;
    }

    public String getNatureFormat() {
        return this.natureFormat;
    }

    public String getMintNatureFormat() {
        return this.mintNatureFormat;
    }

    public String getNormalIvColour() {
        return this.normalIvColour;
    }

    public String getHyperIvColour() {
        return this.hyperIvColour;
    }

    public String getShinyTrueFormat() {
        return this.shinyTrueFormat;
    }

    public String getShinyFalseFormat() {
        return this.shinyFalseFormat;
    }

    public String getNotApplicable() { return notApplicable; }
}
