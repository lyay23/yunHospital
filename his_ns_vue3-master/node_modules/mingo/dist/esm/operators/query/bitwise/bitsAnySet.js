import { processBitwiseQuery } from "./_internal";
const $bitsAnySet = (selector, value, _options) => processBitwiseQuery(selector, value, (result, _) => result > 0);
export {
  $bitsAnySet
};
