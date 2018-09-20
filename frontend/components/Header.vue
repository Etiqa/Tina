<template>
  <div>
    <div class="row">
      <nuxt-link to="/">
        <h1>Meta config</h1>
      </nuxt-link>
    </div>

    <div>
      Breadcrumbs:
      <ul class="list-inline">
        <li v-for="(b, i) in bc" :key="i" class="list-inline-item">
          <span v-if="b.url">
            <nuxt-link :to="b.url">{{ b.name }}</nuxt-link>
          </span>
          <span v-else>
            {{ b.name }}
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

  <script>
export default {
  props: {
    bc: {
      default: () => [],
      type: Array
    }
  }
}
</script>


<style>
</style>
