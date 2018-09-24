<template>
  <div>
    <button class="btn btn-success" @click="onClick()"><i class="fas fa-plus-square" /> Add Directory</button>
  </div>
</template>

<script>
export default {
  props: {
    onClick: {
      default: () => ({}),
      type: Function
    }
  }
}
</script>
<style>
</style>
