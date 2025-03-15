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
      return this.isRecommended && !this.isBooked ? seatRecommendImage : this.isBooked ? seatBookedImage : seatImage;
    }
  }
};
</script>

<style scoped>

.seat {
  width: 3vw;  /* Adjusted size for a better appearance */
  height: 7vh;
  cursor: pointer;
  border-radius: 10px;  /* Rounded corners for a softer look */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);  /* Subtle shadow effect */
  transition: transform 0.3s ease, box-shadow 0.3s ease;  /* Smooth transition for hover effects */
}

.seat:hover {
  transform: translateY(-5px);  /* Slight upward movement on hover */
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2);  /* Deeper shadow effect */
}

.seat:active {
  transform: translateY(1px);  /* Slight downward movement when clicked */
}

</style>
