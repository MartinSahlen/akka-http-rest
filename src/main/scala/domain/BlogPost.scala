package domain

import com.github.nscala_time.time.Imports._
import io.swagger.annotations.{ApiModel, ApiModelProperty}

@ApiModel
case class BlogPost(
  @ApiModelProperty(value = "Published")
  published: Boolean,

  @ApiModelProperty(value = "Publish date")
  publishDate: DateTime,

  @ApiModelProperty(value = "Title")
  title: String,

  @ApiModelProperty(value = "Intro")
  intro: String,

  @ApiModelProperty(value = "Image")
  image: String,

  @ApiModelProperty(value = "Content")
  content: String,

  @ApiModelProperty(value = "Slug")
  slug: String,

  @ApiModelProperty(value = "Id")
  id: String
)

object BlogPost {

}
