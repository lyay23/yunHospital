
<template>
  <div>
	  {{data}}
  <ul>
      <li v-for="(item, index) in list" :key="index">
        {{ item.constantTypeName }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { fetchData } from '../../utils/api'



const data = ref({});
const list=ref([])

onMounted(async () => {
  const result = await fetchData('/constant/page',null);
  data.value = result
  list.value=result.data.records
});
</script>