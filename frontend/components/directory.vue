<template>
  <div class="col-md-4">
    <h3>{{ directoryId }} - {{ name }}</h3>
    <ul>
      <li>{{ elements }} elements</li>

      <li><nuxt-link :to="url">open</nuxt-link></li>
    </ul>
  </div>
</template>
<script>
export default {
  props: {
    directoryId: {
      type: String,
      default: ""
    },
    name: {
      type: String,
      default: ""
    },
    elements: {
      type: Number,
      default: 0
    }
  },
  computed: {
    url() {
      return `/directories/${this.directoryId}`
    }
  }
}
</script>
<style>
</style>
