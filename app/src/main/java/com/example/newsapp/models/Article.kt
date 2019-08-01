package com.example.newsapp.models

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("webTitle")
    @Expose
    val title: String,
    @SerializedName("sectionName")
    @Expose
    val section: String,
    @SerializedName("webPublicationDate")
    @Expose
    val publicationDate: String,
    @SerializedName("webUrl")
    @Expose
    val url: String,
    val _author: String = "",
    val _imgUrl: String = ""
) {
    @SerializedName("tags")
    @Expose
    lateinit var tags: List<Tag>
    @SerializedName("fields")
    @Expose
    var fields: Field? = null

    val author: String
        get() = if (tags.isNotEmpty()) tags[0].author else if (_author != "") _author else "Author unknown"

    val imgUrl: String
        get() = fields?.thumbnail ?: _imgUrl

    var imgBitmap: Bitmap? = null

    fun toCommaSeparatedString(): String {
        return "$title,$section,$publicationDate,$url,$author,$imgUrl"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (title != other.title) return false
        if (section != other.section) return false
        if (publicationDate != other.publicationDate) return false
        if (url != other.url) return false
        if (_author != other._author) return false
        if (_imgUrl != other._imgUrl) return false
        if (tags != other.tags) return false
        if (fields != other.fields) return false
        if (imgBitmap != other.imgBitmap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + section.hashCode()
        result = 31 * result + publicationDate.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + _author.hashCode()
        result = 31 * result + _imgUrl.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + (fields?.hashCode() ?: 0)
        result = 31 * result + (imgBitmap?.hashCode() ?: 0)
        return result
    }

    data class Tag(
        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("webTitle")
        @Expose
        val author: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Tag

            if (id != other.id) return false
            if (author != other.author) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + author.hashCode()
            return result
        }
    }

    data class Field(
        @SerializedName("thumbnail")
        @Expose
        val thumbnail: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Field

            if (thumbnail != other.thumbnail) return false

            return true
        }

        override fun hashCode(): Int {
            return thumbnail.hashCode()
        }
    }
}
