<template>
  <div class="container">
    <Header />
    <div class="row"/>
    <Directory v-for="(dir, i) in directories" :key="i" :name="dir.name" :elements="dir.services.length" :directory-id="dir.id" />
  </div>
</template>

<script>
import Directory from "../components/directory"
import Header from "../components/Header"
import { directories } from "../services/requests"

export default {
  components: { Directory, Header },
  data() {
    return {
      directories: []
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      return directories()
        .then(data => {
          console.log(data)
          this.$set(this, "directories", data)
        })
        .catch(error => console.log("error", error))
    }
  }
}
</script>

<style>
</style>
