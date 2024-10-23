package mikufan.cx.conduit.frontend.logic.repo.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import mikufan.cx.conduit.common.ArticlesRsp

interface ArticleApi {
  @GET("articles")
  suspend fun getArticles(
    @Query tag: String? = null,
    @Query author: String? = null,
    @Query favorited: String? = null,
    @Query limit: Int? = null,
    @Query offset: Int? = null
  ): ArticlesRsp
}