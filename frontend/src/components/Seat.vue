<template>
  <img
      :src="getSeatImage"
      :alt="number"
      class="seat"
      :draggable="isRecommended"
  @dragstart="dragStart"
  @dragover.prevent
  @drop="drop"
  />
</template>

<script>
import seatImage from "@/assets/seat.png";
import seatBookedImage from "@/assets/seat-booked.png";
import seatRecommendImage from "@/assets/seat-recommend.png";

export default {
  props: ["number", "isBooked", "isRecommended"],
  methods: {
    dragStart(event) {
      if (!this.isRecommended) {
        console.log(`❌ Dragging not allowed: ${this.number} is not recommended.`);
        return;
      }

      console.log(`Dragging seat: ${this.number}, Recommended: ${this.isRecommended}`);

      event.dataTransfer.setData("seat", JSON.stringify({
        number: this.number,
        isRecommended: this.isRecommended
      }));
    },
    drop(event) {
      const droppedSeat = JSON.parse(event.dataTransfer.getData("seat"));
      if (droppedSeat.number === this.number) return;
      console.log(`Swapping seat ${droppedSeat.number} with ${this.number}`);
      this.$emit("swapSeats", droppedSeat.number, this.number);
    }
  },
  computed: {
    getSeatImage() {
      return this.isRecommended ? seatRecommendImage : this.isBooked ? seatBookedImage : seatImage;
    }
  }
};
</script>
