<template>
  <img
      :src="isBooked ? '/seat-booked.png' : '/seat.png'"
      :alt="number"
      class="seat"
      :class="classType"
      @click="toggleBooking"
  />
</template>

<script>
export default {
  props: ["number", "isBooked", "classType"],
  methods: {
    toggleBooking() {
      this.$emit("toggle");
    }
  }
};
</script>

