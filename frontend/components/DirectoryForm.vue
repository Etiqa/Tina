<template>
  <div class="col-md-6 mt-3">
    <form @submit.prevent="onSubmit">
      <div class="form-group">
        <label for="name">Directory name</label>
        <input v-model="dirName" name="name" type="text" class="form-control" value="" required>
      </div>

      <div class="btn-group" role="group">
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
