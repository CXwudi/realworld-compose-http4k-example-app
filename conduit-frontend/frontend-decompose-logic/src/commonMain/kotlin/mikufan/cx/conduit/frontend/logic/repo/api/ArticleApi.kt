package mikufan.cx.conduit.frontend.logic.repo.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import mikufan.cx.conduit.common.ArticleRsp
import mikufan.cx.conduit.common.ArticlesRsp
import mikufan.cx.conduit.common.CreateArticleReq
import mikufan.cx.conduit.frontend.logic.repo.api.util.ConduitResponse

interface ArticleApi {
  @GET("articles")
  suspend fun getArticles(
    @Query tag: String? = null,
    @Query author: String? = null,
    @Query favorited: String? = null,
    @Query limit: Int? = null,
    @Query offset: Int? = null
  ): ConduitResponse<ArticlesRsp>

  @POST("articles")
  suspend fun createArticle(
    @Body body: CreateArticleReq
  ) : ConduitResponse<ArticleRsp>
}