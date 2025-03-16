<template>
  <v-container fluid class="seating-container">
    <v-row justify="center" align="start" class="main-content">
      <!-- Left Sidebar: Recommendations -->
      <v-col cols="3" class="side-panel">
        <RecsPanel @filtersUpdated="updateSeats" />
      </v-col>

      <!-- Middle: Seating Layout -->

      <v-col cols="6" class="seat-layout">

        <div v-for="(row, rowIndex) in seats" :key="rowIndex" class="seat-row">
          <!-- Left Group -->
          <div class="seat-group left-group">
            <Seat
                v-for="(seat, seatIndex) in row.seats.filter(s => ['A', 'B', 'C', 'D'].includes(s.position))"
                :key="seatIndex"
                :number="seat.position"
                :isBooked="seat.booked"
                :isRecommended="seat.recommended"
                class="seat-item"
                @swapSeats="swapSeats"
                @seatCountUpdated="updateSeatCount"
            />
          </div>

          <!-- Aisle -->
          <div class="aisle"></div>

          <!-- Right Group -->
          <div class="seat-group right-group">
            <Seat
                v-for="(seat, seatIndex) in row.seats.filter(s => ['E', 'F', 'G', 'H'].includes(s.position))"
                :key="seatIndex"
                :number="seat.position"
                :isBooked="seat.booked"
                :isRecommended="seat.recommended"
                class="seat-item"
                @swapSeats="swapSeats"
                @seatCountUpdated="updateSeatCount"
            />
          </div>
        </div>
      </v-col>

      <!-- Right Sidebar: Ticket Panel -->
      <v-col cols="3" class="side-panel">
        <TicketPanel />
      </v-col>
    </v-row>

    <!-- Bottom Row: Legend -->
    <v-row justify="center" class="legend-row">
      <v-col cols="6">
        <Legend />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";
import Seat from "@/components/Seat.vue";
import RecsPanel from "@/components/RecsPanel.vue";
import TicketPanel from "@/components/TicketPanel.vue";
import Legend from "@/components/Legend.vue";

export default {
  components: {TicketPanel, Seat, RecsPanel, Legend },
  data() {
    return {
      seats: [],
      bookedSeats: [],
      recommendedSeats: [],
      availableSeats: [],
    };
  },
  async created() {
    await this.loadSeats();
  },
  methods: {
    async loadSeats() {
      try {
        const response = await axios.get("/api/seats/getSeatsByFlight", {
          params: {
            flightId: this.$route.query.flightId,
            planeId: this.$route.query.planeId,
          },
        });
        this.availableSeats = response.data.availableSeats;
        this.bookedSeats = response.data.bookedSeats;
        this.recommendedSeats = response.data.recommendedSeats;
        this.generateSeats();
      } catch (error) {
        console.error("❌ Error loading seats:", error);
      }
    },
    updateSeats(filteredSeats) {
      this.availableSeats = filteredSeats.filter(seat => !seat.booked && !seat.recommended);
      this.recommendedSeats = filteredSeats.filter(seat => seat.recommended);
      this.generateSeats();
    },
    generateSeats() {
      localStorage.setItem('recommendedSeats', JSON.stringify(this.recommendedSeats));

      const seatMap = new Map(this.bookedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true]));
      const recommendedSeatMap = new Map(this.recommendedSeats.map(seat => [`${seat.row}${seat.seat_column}`, true])); // Muuda väärtuseks true
      const totalRows = 11;

      this.seats = Array.from({ length: totalRows }, (_, rowIndex) => {
        const row = rowIndex + 1;
        let seatPositions;

        if (row === 1) {
          seatPositions = ["C", "D", "E", "F"];
        } else if (row === 5 || row === 11) {
          seatPositions = ["D", "E"];
        } else {
          seatPositions = ["A", "B", "C", "D", "E", "F", "G", "H"];
        }
        console.log(seatMap + "sm")
        console.log(recommendedSeatMap + "rm")

        return {
          row,
          seats: seatPositions.map(pos => {
            const key = `${row}${pos}`;
            return {
              position: pos,
              booked: seatMap.has(key),
              recommended: recommendedSeatMap.has(key),
            }
          })
        };
      });
    },
    updateSeatCount(newSeatCount) {
      this.seatCount = newSeatCount;
      localStorage.setItem("seatCount", this.seatCount);
    },

    swapSeats(fromSeat, toSeat) {
      let fromSeatObj = null;
      let toSeatObj = null;
      for (const row of this.seats) {
        for (const seat of row.seats) {
          const seatKey = `${row.row}${seat.position}`;
          if (seatKey === fromSeat) fromSeatObj = seat;
          if (seatKey === toSeat) toSeatObj = seat;
        }
      }

      if (fromSeatObj.recommended && !toSeatObj.recommended && !toSeatObj.booked) {
        fromSeatObj.recommended = false;
        toSeatObj.recommended = true;
      }
    },
  }
};
</script>
<style scoped>
.seating-container {
background: linear-gradient(to bottom, #6FA1FF, #87BFFF, #A3CDFF, #BEDCFF);
height: 100vh; /* Full-page height */
}

/* Flexbox for Row Layout */
.main-content {
display: flex;
align-items: flex-start;
}

/* Seat Groups */
.seat-row {
display: flex;
align-items: center;
justify-content: center;
margin-bottom: 10px;
}

.seat-group {
display: flex;
gap: 1vh;
}

/* The aisle space */
.aisle {
width: 5vh; /* Space between seat groups */
}


</style>
