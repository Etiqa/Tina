<template>
  <div>
    <form @submit.prevent="onSubmit">
      <div>
        <label for="name">Name</label>
        <input v-model="dirName" name="name" type="text" value="" required>
      </div>

      <div>
        <button class="btn btn-success" type="submit">Save</button>
        <button class="btn btn-primary" @click.prevent="onClickCancel">Cancel</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  props: {
    onSave: {
      default: () => () => ({}),
      type: Function
    },
    onClickCancel: {
      default: () => () => ({}),
      type: Function
    }
  },
  data() {
    return {
      dirName: null
    }
  },
  methods: {
    onSubmit() {
      if (typeof this.onSave === "function") {
        this.onSave({ name: this.dirName })
      }
    }
  }
}
</script>

<style>
</style>
