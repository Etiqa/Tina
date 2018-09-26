<template>
  <div>
    <h3>Sign Up</h3>
    <form @submit.prevent="signup">
      <div v-if="!isValid()">
        <ul>
          <li v-for="(error, i) in errors" :key="i"> {{ error }}</li>
        </ul>
      </div>

      <div class="form-group">
        <label for="user-email">Email address</label>
        <input id="user-email" v-model="email" type="email" class="form-control" aria-describedby="emailHelp" placeholder="Enter email" required>
        <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small>
      </div>
      <div class="form-group">
        <label for="confirm-email">Repeat email</label>
        <input id="confirm-email" v-model="confirmEmail" type="email" class="form-control" aria-describedby="emailHelp" placeholder="Enter email" required>
      </div>
      <div class="form-group">
        <label for="user-password">Password</label>
        <input id="user-password" v-model="password" type="password" class="form-control" placeholder="Password" pattern=".{5,10}" title="Password must be between 5 and 10" required>
      </div>
      <div class="form-group">
        <label for="confirm-password">Repeat password</label>
        <input id="confirm-password" v-model="confirmPassword" type="password" class="form-control" placeholder="Confirm password" pattern=".{5,10}" title="Password must be between 5 and 10" required>
      </div>
      <button type="submit" class="btn btn-primary">Submit</button>
      <small><a href="#" @click.prevent="toggleSignin"> Already registered?</a></small>
    </form>
  </div>
</template>

<script>
import { signup } from "../services/requests"
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
      confirmEmail: "",
      password: "",
      confirmPassword: "",
      errors: []
    }
  },
  methods: {
    toggleSignin() {
      this.onToggleSignin()
    },
    validateConfirmEmail() {
      return this.email === this.confirmEmail
    },
    validateConfirmPassword() {
      return this.password === this.confirmPassword
    },
    checkForm() {
      this.errors = []

      if (!this.validateConfirmEmail()) {
        this.errors.push("The two email addresses must match.")
      }

      if (!this.validateConfirmPassword()) {
        this.errors.push("The two password must match.")
      }
    },
    isValid() {
      return !this.errors.length
    },
    signup() {
      this.checkForm()
      if (this.isValid()) {
        const { email, password } = this
        signup({ email, password })
          .then(resp => console.log(resp))
          .catch(({ error }) => {
            console.log(error)
            if (error === "ER_DUP_ENTRY") {
              this.errors.push("Email already in use")
            } else {
              this.errors.push("There was a problem with the signup")
            }
          })
      }
    }
  }
}
</script>

<style>
</style>
