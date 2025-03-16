<script setup>
import { ref } from "vue";

const seatLegend = ref([
  { color: "green", label: "Available" },
  { color: "yellow", label: "Recommended" },
  { color: "red", label: "Booked" }
]);
</script>

<template>
  <v-card class="legend-card pa-1">
    <v-card-title class="text-h9 text-center">Seat Selection Guide</v-card-title>

    <v-row align="center" justify="center" class="legend-container">
      <v-col v-for="item in seatLegend" :key="item.label" cols="auto" class="d-flex align-center">
        <v-avatar size="14" :color="item.color" class="mr-2"></v-avatar>
        <span>{{ item.label }}</span>
      </v-col>
    </v-row>

    <v-card-text class="text-center text-body-5">
      Toggle an available seat to select it, or use filters to see recommended options.
      Adjust your ticket count to choose more seats!
    </v-card-text>
  </v-card>
</template>

<style scoped>
.legend-card {
  background-color: #f9f9f9;
  opacity: 0.9;
  height: 20vh;
  padding-bottom: 1vh;
}

.legend-container {
  gap: 0.5vh;
}
</style>
