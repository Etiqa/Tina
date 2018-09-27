<template>
  <div class="container">
    <div class="row">
      <div class="col">
        <nuxt-link to="/dashboard">
          <h1>Tina</h1>
        </nuxt-link>
      </div>
    </div>
    <div class="row">
      <div v-if="bc.length" class="col">
        <ul class="list-inline">
          <li class="list-inline-item"> <small>>></small> </li>
          <li v-for="(b, i) in bc" :key="i" class="list-inline-item">
            <span v-if="b.url">
              <nuxt-link :to="b.url"> <small> {{ b.name }} </small> </nuxt-link>
            </span>
            <small v-else>
              {{ b.name }}
            </small>
            <small v-if="i + 1 < bc.length"> // </small>
          </li>
        </ul>
      </div>
      <div v-if="logged" class="col-md-4">
        <ul class=" nav nav-pills card-header-pills">
          <li class="nav-item">
            <nuxt-link class="nav-link" to="/parse-fn">Parse functions</nuxt-link>
          </li>
          <li>
            <button type="button" class="btn btn-danger" @click.prevent="logout">Logout</button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie"

export default {
  props: {
    bc: {
      default: () => [],
      type: Array
    },
    logged: {
      default: true,
      type: Boolean
    }
  },
  methods: {
    logout() {
      Cookies.remove("auth-token", { path: "/" })
      this.$router.replace("/")
    }
  }
}
</script>


<style>
</style>
