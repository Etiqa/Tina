<template>
  <div class="col-md-4">
    <h3>{{ name }}</h3>
    <ul class="list-group">
      <li class="list-group-item d-flex justify-content-between align-items-center">elements <span class="badge badge-primary badge-pill">{{ elements }}</span></li>
      <li class="list-group-item"><nuxt-link :to="url">open</nuxt-link></li>
      <li v-if="!del" class="list-group-item"><button class="btn btn-danger" @click.prevent="startDelete">Delete</button></li>
      <li v-else class="list-group-item">
        <div >
          Are you sure?
        </div>
        <div class="btn-group" role="group">
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
