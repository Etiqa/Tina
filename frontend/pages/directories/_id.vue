<template>
  <div class="container">
    <Header :bc="bc" />
    <h2>{{ name }}!!!!</h2>

    <div class="row">

      <Service v-for="(serv, i) in services" :key="i" :dir-id="id" :id="i" :name="serv.name" :url="serv.url" :update-fn="fetchData"/>
    </div>
    <div class="row">
      <div v-if="!addServ" class="col">
        <button class="btn btn-success" @click.prevent="toggleAdd"><i class="fas fa-plus-square" /> Add Service</button>
      </div>
      <div v-else class="col-md-6">
        <ServiceForm v-bind="{ onClickCancel: toggleAdd, onSave: addService }"/>
      </div>
    </div>
  </div>
</template>

<script>
import Header from "../../components/Header"
import Service from "../../components/Service"
import ServiceForm from "../../components/ServiceForm"
import { directory, addService } from "../../services/requests"

export default {
  components: { Header, Service, ServiceForm },
  data() {
    return {
      services: [],
      name: "",
      addServ: false
    }
  },
  computed: {
    id() {
      return this.$route.params.id
    },
    bc() {
      return [
        { url: "/dashboard", name: "dashboard" },
        {
          name: this.name
        }
      ]
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      directory(this.id).then(data => {
        this.$set(this, "name", data.name)
        this.$set(this, "services", data.services)
      })
    },
    toggleAdd() {
      this.$set(this, "addServ", !this.addServ)
    },
    addService({ url, name }) {
      this.$set(this, "addServ", false)
      addService(this.id, { url, name }).then(this.fetchData)
    }
  }
}
</script>
<style>
</style>
