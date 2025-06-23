<script>
export default {
  name: 'QuestionCard',
  props: {
    question: Object
  },
  template: `
    <article class="p-6 border rounded-2xl hover:shadow-md transition cursor-pointer">
      <h3 class="font-semibold text-lg mb-2 line-clamp-2">{{ question.title }}</h3>
      <p class="text-sm text-gray-500 line-clamp-3" v-html="question.body"></p>
      <div class="mt-4 flex items-center justify-between text-xs text-gray-400">
        <span class="flex items-center gap-1"><i class="fas fa-eye"></i> {{ question.viewCount }}</span>
        <time :datetime="question.createdAt">{{ new Date(question.createdAt).toLocaleDateString() }}</time>
      </div>
    </article>`
}
</script>