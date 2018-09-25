<template>
  <div class="col-md-4">
    <h3>{{ name }}</h3>
    <ul>
      <li>{{ elements }} elements</li>
      <li><nuxt-link :to="url">open</nuxt-link></li>
      <li v-if="!del"><button class="btn btn-danger" @click.prevent="startDelete">Delete</button></li>
      <li v-else>
        <div>
          Delete all {{ elements }} elements?
        </div>
        <div>
          <button class="btn btn-danger" @click.prevent="deleteDirectory">Delete</button>
          <button class="btn btn-primary" @click.prevent="cancelDelete">Cancel</Button>
        </div>
      </li>
    </ul>
  </div>
</template>
<script>
import { deleteDirectory } from "../services/requests"
export default {
  props: {
    directoryId: {
      type: Number,
      default: 0
    },
    name: {
      type: String,
      default: ""
    },
    elements: {
      type: Number,
      default: 0
    },
    updateFn: {
      type: Function,
      default: () => () => ({})
    }
  },
  data() {
    return {
      del: false
    }
  },
  computed: {
    url() {
      return `/directories/${this.directoryId}`
    }
  },
  methods: {
    startDelete() {
      this.$set(this, "del", true)
    },
    cancelDelete() {
      this.$set(this, "del", false)
    },
    deleteDirectory() {
      deleteDirectory(this.directoryId).then(this.updateFn)
    }
  }
}
</script>
<style>
</style>
