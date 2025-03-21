<template>
  <img
      :alt="number"
      :draggable="isRecommended"
      :src="getSeatImage"
      class="seat"
      @click="toggleRecommended"
      @dragstart="dragStart"
      @drop="drop"
      @dragover.prevent
  />
</template>

<script>
import seatImage from "@/assets/seat.png";
import seatBookedImage from "@/assets/seat-booked.png";
import seatRecommendImage from "@/assets/seat-recommend.png";

export default {
  props: ["number", "isBooked", "isRecommended"],
  data() {
    return {
      localRecommended: this.isRecommended,
    };
  },
  methods: {
    dragStart(event) {
      if (!this.localRecommended) {
        console.log(`❌ Dragging not allowed: ${this.number} is not recommended.`);
        return;
      }

      console.log(`Dragging seat: ${this.number}, Recommended: ${this.localRecommended}`);

      event.dataTransfer.setData("seat", JSON.stringify({
        number: this.number,
        isRecommended: this.localRecommended
      }));
    },
    drop(event) {
      const droppedSeat = JSON.parse(event.dataTransfer.getData("seat"));
      if (droppedSeat.number === this.number) return;
      console.log(`Swapping seat ${droppedSeat.number} with ${this.number}`);
      this.$emit("swapSeats", droppedSeat.number, this.number);
    },
    toggleRecommended() {
      if (this.isBooked) return;

      this.localRecommended = !this.localRecommended;
      this.$emit("update:isRecommended", this.localRecommended);

      let seatCount = Number(localStorage.getItem("seatCount")) || 1;
      seatCount = this.localRecommended ? seatCount + 1 : seatCount - 1;

      localStorage.setItem("seatCount", seatCount);
      this.$emit("seatCountUpdated", seatCount);
    }
  },
  computed: {
    getSeatImage() {
      return this.localRecommended && !this.isBooked ? seatRecommendImage : this.isBooked ? seatBookedImage : seatImage;
    }
  },
  watch: {
    isRecommended(newValue) {
      this.localRecommended = newValue;
    }
  }
};
</script>


<style scoped>

.seat {
  width: 3vw;
  height: 5vh;
  cursor: pointer;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* Smooth transition for hover effects */
}

.seat:hover {
  transform: translateY(-5px); /* Slight upward movement on hover */
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.2); /* Deeper shadow effect */
}

.seat:active {
  transform: translateY(1px); /* Slight downward movement when clicked */
}

</style>
