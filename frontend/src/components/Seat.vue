<template>
  <img
      :src="isBooked ? seatBookedImage : seatImage"
      :alt="number"
      class="seat"
      :class="classType"
      @click="toggleBooking"
  />
</template>

<script>
import seatImage from '@/assets/seat.png';
import seatBookedImage from '@/assets/seat-booked.png';

export default {
  props: ["number", "isBooked", "classType"],
  methods: {
    toggleBooking() {
      this.$emit("toggle");
    }
  },
  data() {
    return {
      seatImage,
      seatBookedImage
    };
  }
};
</script>
