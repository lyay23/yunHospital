import { processBitwiseQuery } from "./_internal";
const $bitsAllSet = (selector, value, _options) => processBitwiseQuery(selector, value, (result, mask) => result == mask);
export {
  $bitsAllSet
};
