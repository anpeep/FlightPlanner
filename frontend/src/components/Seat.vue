<template>
  <img
      :src="getSeatImage"
      :alt="number"
      class="seat"
      :class="classType"
  @click="toggleBooking"
  />
</template>

<script>
import seatImage from "@/assets/seat.png";
import seatBookedImage from "@/assets/seat-booked.png";
import seatRecommendImage from "@/assets/seat-recommend.png";

export default {
  props: ["number", "isBooked", "isRecommended", "classType"],
  computed: {
    getSeatImage() {
      console.log("isRecommended:", this.isRecommended);  // Logi väärtus
      if (this.isRecommended) {
        return seatRecommendImage; // Ainult 1 PNG recommended seat'idele
      }
      return this.isBooked ? seatBookedImage : seatImage; // Kasutab õigeid pilte
    }
},
  methods: {
    toggleBooking() {
      this.$emit("toggle");
    }
  }
};
</script>
