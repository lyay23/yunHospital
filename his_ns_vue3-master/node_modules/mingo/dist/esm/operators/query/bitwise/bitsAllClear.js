import { processBitwiseQuery } from "./_internal";
const $bitsAllClear = (selector, value, _options) => processBitwiseQuery(selector, value, (result, _) => result == 0);
export {
  $bitsAllClear
};
