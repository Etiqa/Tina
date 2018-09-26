<template>
  <div>
    <div class="row">
      <nuxt-link to="/dashboard">
        <h1>Meta config</h1>
      </nuxt-link>
    </div>

    <div class="row">
      <div v-if="bc.length" class="col-md-11">
        Breadcrumbs:
        <ul class="list-inline">
          <li v-for="(b, i) in bc" :key="i" class="list-inline-item">
            <span v-if="b.url">
              <nuxt-link :to="b.url">{{ b.name }}</nuxt-link>
            </span>
            <span v-else>
              {{ b.name }}
            </span>

            <span v-if="i + 1 < bc.length"> // </span>
          </li>
        </ul>
      </div>
      <div v-if="logged" class="col-md-1">
        <button type="button" class="btn btn-danger" @click.prevent="logout">Logout</button>
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
