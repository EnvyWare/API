package com.envyful.api.discord.yaml;

import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.discord.DiscordEmbed;
import com.envyful.api.discord.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.awt.*;
import java.util.*;
import java.util.List;

@ConfigSerializable
public class DiscordEmbedConfig extends AbstractYamlConfig {

    private String title;
    private String description;
    private String url;
    private DiscordColor color;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private Author author;
    private Map<String, Field> fields;

    public DiscordEmbedConfig() {
    }

    public DiscordEmbedConfig(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.url = builder.url;
        this.color = builder.color;
        this.footer = builder.footer;
        this.thumbnail = builder.thumbnail;
        this.image = builder.image;
        this.author = builder.author;
        this.fields = new HashMap<>();

        for (Field field : builder.fields) {
            this.fields.put("example-" + (this.fields.size() + 1), field);
        }
    }

    /**
     *
     * Converts the {@link DiscordEmbed} to a {@link JSONObject}
     *
     * @return the JSON version of the embed
     */
    public JsonObject toJson() {
        JsonObject jsonEmbed = new JsonObject();

        jsonEmbed.addProperty("title", this.title);
        jsonEmbed.addProperty("description", this.description);
        jsonEmbed.addProperty("url", this.url);

        Footer writtenFooter = this.footer;
        Image writtenImage = this.image;
        Thumbnail writtenThumbnail = this.thumbnail;
        Author writtenAuthor = this.author;
        List<Field> writtenFields = List.copyOf(this.fields.values());
        Color writtenColor = this.color.getColor();

        if (writtenColor != null) {
            int rgb = writtenColor.getRed();
            rgb = (rgb << 8) + writtenColor.getGreen();
            rgb = (rgb << 8) + writtenColor.getBlue();
            jsonEmbed.addProperty("color", rgb);
        }

        if (writtenFooter != null) {
            JsonObject jsonFooter = new JsonObject();

            jsonFooter.addProperty("text", writtenFooter.getText());
            jsonFooter.addProperty("icon_url", writtenFooter.getIconUrl());
            jsonEmbed.add("footer", jsonFooter);
        }

        if (writtenImage != null && !writtenImage.isEmpty()) {
            JsonObject jsonImage = new JsonObject();

            jsonImage.addProperty("url", writtenImage.getUrl());
            jsonEmbed.add("image", jsonImage);
        }

        if (writtenThumbnail != null && !writtenThumbnail.isEmpty()) {
            JsonObject jsonThumbnail = new JsonObject();

            jsonThumbnail.addProperty("url", writtenThumbnail.getUrl());
            jsonEmbed.add("thumbnail", jsonThumbnail);
        }

        if (writtenAuthor != null && !writtenAuthor.isEmpty()) {
            JsonObject jsonAuthor = new JsonObject();

            jsonAuthor.addProperty("name", writtenAuthor.getName());
            jsonAuthor.addProperty("url", writtenAuthor.getUrl());
            jsonAuthor.addProperty("icon_url", writtenAuthor.getIconUrl());
            jsonEmbed.add("author", jsonAuthor);
        }

        JsonArray jsonFields = new JsonArray();
        for (Field field : writtenFields) {
            JsonObject jsonField = new JsonObject();

            jsonField.addProperty("name", field.getName());
            jsonField.addProperty("value", field.getValue());
            jsonField.addProperty("inline", field.isInline());

            jsonFields.add(jsonField);
        }

        jsonEmbed.add("fields", jsonFields);
        return jsonEmbed;
    }

    public static Builder builder() {
        return new Builder();
    }

    @ConfigSerializable
    public static class DiscordColor {

        private transient Color cachedColor;
        private int red;
        private int green;
        private int blue;
        private int alpha;

        public DiscordColor(int red, int green, int blue, int alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public DiscordColor(Color color) {
            this.red = color.getRed();
            this.green = color.getGreen();
            this.blue = color.getBlue();
            this.alpha = color.getAlpha();
        }

        public DiscordColor() {
        }

        public Color getColor() {
            if (this.cachedColor == null) {
                this.cachedColor = new Color(this.red, this.green, this.blue, this.alpha);
            }

            return this.cachedColor;
        }
    }

    @ConfigSerializable
    public static class Footer {

        private String text;
        private String iconUrl;

        public Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }

        public Footer() {
        }

        public String getText() {
            return text;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public boolean isEmpty() {
            return (text == null || text.isEmpty()) &&
                    (iconUrl == null || iconUrl.isEmpty());
        }

    }

    @ConfigSerializable
    public static class Thumbnail {

        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }

        public Thumbnail() {
        }

        public String getUrl() {
            return url;
        }

        public boolean isEmpty() {
            return url == null || url.isEmpty();
        }
    }

    @ConfigSerializable
    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }

        public Image() {
        }

        public String getUrl() {
            return url;
        }

        public boolean isEmpty() {
            return url == null || url.isEmpty();
        }
    }

    @ConfigSerializable
    public static class Author {
        private String name;
        private String url;
        private String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }

        public Author() {
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public boolean isEmpty() {
            return (name == null || name.isEmpty()) &&
                    (url == null || url.isEmpty()) &&
                    (iconUrl == null || iconUrl.isEmpty());
        }
    }

    @ConfigSerializable
    public static class Field {
        private String name;
        private String value;
        private boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public Field() {
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }
    }

    public static class Builder {

        private String title;
        private String description;
        private String url;
        private DiscordColor color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private List<Field> fields = new ArrayList<>();

        protected Builder() {
            // Reduce access to static method
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder color(DiscordColor color) {
            this.color = color;
            return this;
        }

        public Builder footer(Footer footer) {
            this.footer = footer;
            return this;
        }

        public Builder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder author(Author author) {
            this.author = author;
            return this;
        }

        public Builder fields(Field... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public DiscordEmbedConfig build() {
            return new DiscordEmbedConfig(this);
        }
    }
}
