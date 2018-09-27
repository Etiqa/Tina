<template>
  <div>
    <h3>Sign In</h3>
    <form @submit.prevent="signin">
      <div class="form-group">
        <label for="user-email">Email address</label>
        <input id="user-email" v-model="email" type="email" class="form-control" aria-describedby="emailHelp" placeholder="Enter email" required>
        <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
      </div>
      <div class="form-group">
        <label for="user-password">Password</label>
        <input id="user-pasword" v-model="password" type="password" class="form-control" placeholder="Password" required>
      </div>
      <button type="submit" class="btn btn-primary">Submit</button>
      <small><a href="#" @click.prevent="toggleSignin"> New User?</a></small>
    </form>
  </div>
</template>

<script>
import { signin } from "../services/requests"

export default {
  props: {
    onToggleSignin: {
      default: () => () => ({}),
      type: Function
    }
  },
  data() {
    return {
      email: "",
      password: ""
    }
  },
  methods: {
    toggleSignin() {
      this.onToggleSignin()
    },
    signin() {
      const { email, password } = this
      signin({ email, password }).then(() => this.$router.replace("/dashboard"))
    }
  }
}
</script>

<style>
</style>
