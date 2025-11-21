// src/stores/postDetail.js
import { defineStore } from 'pinia'

export const usePostDetailStore = defineStore('postDetail', {
    state: () => ({
        last: null, // { post, poll }
    }),
    actions: {
        setDetail(payload) {
            this.last = payload
        },
        getByPostId(postId) {
            const idNum = Number(postId)
            if (this.last && Number(this.last.post.id) === idNum) {
                return this.last
            }
            return null
        },
        clear() {
            this.last = null
        }
    },
})
