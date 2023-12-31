package com.example.honjarang.domain.post.controller;


import com.example.honjarang.domain.DateTimeUtils;
import com.example.honjarang.domain.post.dto.*;
import com.example.honjarang.domain.post.entity.Category;
import com.example.honjarang.domain.post.entity.Post;
import com.example.honjarang.domain.post.service.PostService;
import com.example.honjarang.domain.user.entity.Role;
import com.example.honjarang.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PostController.class)
@AutoConfigureRestDocs
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private User user;

    private Post post;

    private CommentListDto commentListDto;


    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("honjarang.kro.kr")
                        .withPort(80))
                .build();
        user = User.builder()
                .email("test@test.com")
                .password("test1234")
                .nickname("테스트")
                .address("서울특별시 강남구")
                .latitude(37.123456)
                .longitude(127.123456)
                .role(Role.ROLE_USER)
                .build();
        user.setIdForTest(1L);
        post = Post.builder()
                .title("테스트")
                .content("콘텐츠")
                .views(0)
                .isNotice(false)
                .category(Category.FREE)
                .user(user)
                .build();
        post.setCreatedAtForTest(DateTimeUtils.parseLocalDateTime("2023-07-29 04:23:23"));
        post.setIdForTest(1L);
        post.setImageForTest("image.jpg");
        commentListDto = CommentListDto.builder()
                .id(1L)
                .content("test")
                .userId(1L)
                .nickname("테스트닉네임")
                .createdAt("2030-01-01 00:00:00")
                .build();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }
    @Test
    @WithMockUser
    @DisplayName("게시글 작성")
    void createPost_Success() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("profile_image", "test.jpg", "image/jpeg", "test".getBytes());
        PostCreateDto postCreateDto = new PostCreateDto("title",Category.FREE,"content");


        // when & then
        mockMvc.perform(multipart("/api/v1/posts")
                        .file("post_image", file.getBytes())
                        .part(new MockPart("title","title".getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("category","FREE".getBytes(StandardCharsets.UTF_8)))
                        .part(new MockPart("content","content".getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isCreated())
                .andDo(document("/posts/create",
                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
                        requestParts(
                                 partWithName("post_image").description("이미지 첨부"),
                                partWithName("title").description("제목"),
                                partWithName("category").description("카테고리"),
                                partWithName("content").description("컨텐트")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 삭제 성공")
    void deletePost_Success() throws Exception {

        // given
        Long postId = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/posts/{id}",postId).
                        contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(document("/posts/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("게시글 ID")
                        )));
    }


    @Test
    @WithMockUser
    @DisplayName("게시글 수정 성공")
    void updatePost_Success() throws Exception {
        // given

        MockMultipartFile file = new MockMultipartFile("post_image","test.jpg","image/jpeg","test".getBytes());
        PostUpdateDto postUpdateDto = new PostUpdateDto( "글제목", "글내용", Category.FREE, false);


        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(PUT, "/api/v1/posts/{id}",1L)
                        .file("post_image",file.getBytes())
                .param("title",postUpdateDto.getTitle())
                .param("content",postUpdateDto.getContent())
                .param("category", String.valueOf(postUpdateDto.getCategory()))
                .param("isNotice", String.valueOf(postUpdateDto.getIsNotice())))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getPostList_Success() throws Exception {

        // given
        List<PostListDto> postListdto = List.of(new PostListDto(post, 10, 10));

        given(postService.getPostList(1,"테스트")).willReturn(postListdto);

        // when & then
        mockMvc.perform(get("/api/v1/posts")
                .param("page","1")
                .param("keyword","테스트"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].user_id").value(1L))
                .andExpect(jsonPath("$[0].user_nickname").value("테스트"))
                .andExpect(jsonPath("$[0].title").value("테스트"))
                .andExpect(jsonPath("$[0].category").value("FREE"))
                .andExpect(jsonPath("$[0].content").value("콘텐츠"))
                .andExpect(jsonPath("$[0].views").value(0))
                .andExpect(jsonPath("$[0].is_notice").value(false))
                .andExpect(jsonPath("$[0].created_at").value("2023-07-29 04:23:23"))
                .andExpect(jsonPath("$[0].like_cnt").value(10))
                .andExpect(jsonPath("$[0].comment_cnt").value(10))
                .andDo(document("/posts/list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("keyword").description("키워드")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("[].user_id").type(JsonFieldType.NUMBER).description("작성자 ID"),
                                fieldWithPath("[].user_nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("[].category").type(JsonFieldType.STRING).description("게시글 카테고리"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("[].views").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("[].is_notice").type(JsonFieldType.BOOLEAN).description("공지 유무"),
                                fieldWithPath("[].created_at").type(JsonFieldType.STRING).description("작성일"),
                                fieldWithPath("[].like_cnt").type(JsonFieldType.NUMBER).description("좋아요수"),
                                fieldWithPath("[].comment_cnt").type(JsonFieldType.NUMBER).description("댓글 수")
                        )
                        ));
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void getPost_Success() throws Exception {

        //given
        PostDto postdto = new PostDto(post, 10, true);

        given(postService.getPost(eq(1L), any(User.class))).willReturn(postdto);

        // when & then
        mockMvc.perform(get("/api/v1/posts/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user_id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트"))
                .andExpect(jsonPath("$.category").value("FREE"))
                .andExpect(jsonPath("$.content").value("콘텐츠"))
                .andExpect(jsonPath("$.nickname").value("테스트"))
                .andExpect(jsonPath("$.views").value(0))
                .andExpect(jsonPath("$.is_notice").value(false))
                .andExpect(jsonPath("$.created_at").value("2023-07-29 04:23:23"))
                .andExpect(jsonPath("$.post_image").value("https://honjarang-bucket.s3.ap-northeast-2.amazonaws.com/postImage/image.jpg"))
                .andExpect(jsonPath("$.is_liked").value(true))
                .andExpect(jsonPath("$.like_cnt").value(10))
                .andDo(document("/posts/detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("user_id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                                fieldWithPath("views").type(JsonFieldType.NUMBER).description("조회수"),
                                fieldWithPath("is_notice").type(JsonFieldType.BOOLEAN).description("공지 유무"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("작성일"),
                                fieldWithPath("post_image").type(JsonFieldType.STRING).description("이미지 주소"),
                                fieldWithPath("is_liked").type(JsonFieldType.BOOLEAN).description("좋아요 유무"),
                                fieldWithPath("like_cnt").type(JsonFieldType.NUMBER).description("좋아요 수")
                        )));
    }

    @Test
    @WithMockUser
    @DisplayName("게시글 좋아요 성공")
    void likePost_Success() throws Exception {

        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/posts/{id}/like", id))
                .andExpect(status().isOk())
        .andDo(document("posts/like",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("id").description("게시글 ID")
                )));
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void creaetComment_Success() throws Exception {

        // given
        CommentCreateDto commentCreateDto = new CommentCreateDto("test");

        // when & then
        mockMvc.perform(post("/api/v1/posts/{id}/comments", 1L)
                 .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andDo(document("posts/comments",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("게시글 ID")
                        ),
                        requestFields(
                          fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() throws Exception {

        // given

        // when & then
        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L))
                .andExpect(status().isOk())
                .andDo(document("posts/comment/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getCommentList_Success() throws Exception {

        // given
        List<CommentListDto> commentListDtoList = List.of(commentListDto);
        given(postService.getCommentList(1L)).willReturn(commentListDtoList);

        //when & then
        mockMvc.perform(get("/api/v1/posts/{id}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("test"))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].nickname").value("테스트닉네임"))
                .andExpect(jsonPath("$[0].createdAt").value("2030-01-01 00:00:00"))
                .andDo(document("posts/comments/list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("댓글 ID"),
                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                        fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("댓글 생성 날짜")
                )
                ));
    }

    @Test
    @DisplayName("페이지 수")
    void getPostsPageCount_Success() throws Exception {
        // given
        given(postService.getPostsPageCount(3,"")).willReturn(10);

        //when & then
        mockMvc.perform(get("/api/v1/posts/page")
                        .param("size","3")
                        .param("keyword",""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10))
                .andDo(document("posts/pageCount",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("size").description("사이즈"),
                                parameterWithName("keyword").description("키워드")
                        )
                ));
    }

    @Test
    @DisplayName("공지 설정하기")
    void noticePost_success() throws Exception {
        mockMvc.perform(put("/api/v1/posts/{id}/notice", 1))
                .andExpect(status().isOk())
                .andDo(document("posts/notice",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("게시글 ID")
                        )
                ));
    }


    }
