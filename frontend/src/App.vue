<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>


    <nav>
      <RouterLink to="/Seat">Seat</RouterLink>
      <RouterLink to="/">Seat</RouterLink>
    </nav>
  </header>

  <RouterView />
</template>
