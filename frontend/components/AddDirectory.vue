<template>
  <div class="col add-dir-button">
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
div.add-dir-button {
  margin-top: 20px;
}
</style>
