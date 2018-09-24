<template>
  <div class="container">
    <Header />
    <div class="row"/>
    <Directory v-for="(dir, i) in directories" :key="i" :name="dir.name" :elements="dir.services.length" :directory-id="dir.id" />
    <AddDirectory v-if="!addDir" v-bind="{ onClick: toggleAdd }" />
    <DirectoryForm v-else v-bind="{ onClickCancel: toggleAdd, onSave: saveDirectory }" />
  </div>
</template>

<script>
import Directory from "../components/directory"
import Header from "../components/Header"
import { directories, addDirectory } from "../services/requests"
import AddDirectory from "../components/AddDirectory"
import DirectoryForm from "../components/DirectoryForm"

export default {
  components: { Directory, Header, AddDirectory, DirectoryForm },
  data() {
    return {
      directories: [],
      addDir: false
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      return directories()
        .then(data => {
          data.forEach(d => console.log(d.j))
          this.$set(this, "directories", data)
        })
        .catch(error => console.log("error", error))
    },
    toggleAdd() {
      this.$set(this, "addDir", !this.addDir)
    },
    closeAdd() {
      this.$set(this, "addDir", false)
    },
    saveDirectory(dirData) {
      addDirectory(dirData).then(() => {
        this.closeAdd()
        this.fetchData()
      })
    }
  }
}
</script>

<style>
</style>
