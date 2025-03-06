<template>
  <input type="date" v-model="selectedDate" @input="updateDate" />
</template>

<script>
export default {
  props: ["modelValue"],
  data() {
    return { selectedDate: this.modelValue };
  },
  watch: {
    modelValue(newValue) {
      this.selectedDate = newValue;
    }
  },
  methods: {
    updateDate() {
      this.$emit("update:modelValue", this.selectedDate);
    }
  }
};
</script>
