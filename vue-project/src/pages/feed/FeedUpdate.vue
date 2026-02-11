<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/utils/api";
import { htmlToText, textToHtml } from "@/utils/htmlText";

const route = useRoute();
const router = useRouter();

const feedId = route.query.id;

const curTitle = ref("");
const curDescriptionHtml = ref("");

const title = ref("");
const description = ref("");

const loading = ref(false);
const fetching = ref(true);
const errorMsg = ref("");

onMounted(async () => {
  try {
    const { data } = await api.get(`/post/${feedId}`);

    curTitle.value = data.ok.post.title ?? "";
    curDescriptionHtml.value = data.ok.post.description ?? "";

    title.value = curTitle.value;
    description.value = htmlToText(curDescriptionHtml.value); // ✅ 텍스트로 변환해서 textarea에
  } catch (err) {
    console.error(err);
    errorMsg.value = "게시글을 불러오지 못했습니다.";
  } finally {
    fetching.value = false;
  }
});

async function submitEdit() {
  if (!title.value.trim() || !description.value.trim()) {
    errorMsg.value = "제목과 내용을 입력해주세요.";
    return;
  }

  loading.value = true;
  errorMsg.value = "";

  try {
    const token = localStorage.getItem("token");

    await api.put(
        `/post/${feedId}`,
        {
          id: feedId,
          title: title.value,
          // ✅ 서버에 저장할 때는 다시 HTML로
          description: textToHtml(description.value),
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
    );

    router.push(`/post/${feedId}`);
  } catch (e) {
    console.error(e);
    errorMsg.value = "수정 중 오류가 발생했습니다.";
  } finally {
    loading.value = false;
  }
}

function resetToCurrent() {
  title.value = curTitle.value;
  description.value = htmlToText(curDescriptionHtml.value); // ✅ reset도 텍스트로 복원
}

function goBack() {
  router.back();
}
</script>

<template>
  <div class="container py-5" style="max-width: 880px">
    <h2 class="fw-bold mb-4 text-center">게시글 수정</h2>

    <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>

    <div v-if="fetching" class="text-center py-5">
      <div class="spinner-border" />
    </div>

    <template v-else>
      <!-- 현재 글 미리보기 -->
      <div class="card shadow-sm mb-4">
        <div class="card-header d-flex justify-content-between align-items-center">
          <strong>현재 게시글</strong>
          <button class="btn btn-sm btn-outline-secondary" @click="goBack">이전으로</button>
        </div>

        <div class="card-body">
          <h4 class="mb-3">{{ curTitle || "제목 없음" }}</h4>
          <div class="text-muted small mb-2">현재 게시글 내용</div>
          <!-- ✅ 미리보기는 HTML 그대로 -->
          <div class="preview-content" v-html="curDescriptionHtml || '<em>내용 없음</em>'"></div>
        </div>
      </div>

      <div class="text-center my-3">
        <span class="badge rounded-pill text-bg-light">아래에서 수정</span>
      </div>

      <!-- 수정 폼 -->
      <div class="card shadow-sm">
        <div class="card-body">
          <div class="form-floating mb-3">
            <input v-model="title" type="text" class="form-control" id="titleInput" placeholder="제목" />
            <label for="titleInput">수정할 제목</label>
          </div>

          <div class="form-floating mb-4">
            <!-- ✅ textarea는 description(텍스트)로 묶어야 함 -->
            <textarea
                v-model="description"
                class="form-control"
                id="descInput"
                placeholder="내용 입력"
                style="height: 240px"
            />
            <label for="descInput">수정할 내용</label>
          </div>

          <div class="d-flex gap-2">
            <button class="btn btn-outline-secondary" type="button" @click="resetToCurrent">
              현재 내용으로 되돌리기
            </button>
            <button class="btn btn-primary ms-auto" :disabled="loading" @click="submitEdit">
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              수정 완료
            </button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<style scoped>
.preview-content {
  line-height: 1.7;
  white-space: pre-line;
  word-break: break-word;
}

textarea.form-control:focus,
input.form-control:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, .25);
}
</style>
