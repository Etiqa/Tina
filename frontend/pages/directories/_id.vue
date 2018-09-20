<template>
  <div class="container">
    <Header :bc="bc" />
    <h2>{{ id }}!!!!</h2>

    <div class="row">

      <Service v-for="(serv, i) in services" :key="i" :name="serv.name" :url="serv.url"/>
    </div>
    <div class="row">
      <div>
        <button class="btn btn-success"><i class="fas fa-plus-square" /> Add Service</button>
      </div>
    </div>
  </div>
</template>

<script>
import Header from "../../components/Header"
import Service from "../../components/Service"
import { directory } from "../../services/requests"

export default {
  components: { Header, Service },
  data() {
    return {
      services: []
    }
  },
  computed: {
    id() {
      return this.$route.params.id
    },
    bc() {
      return [
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
        this.$set(this, "services", data.services)
      })
    }
  }
}
</script>
<style>
</style>
