import { processBitwiseQuery } from "./_internal";
const $bitsAnyClear = (selector, value, _options) => processBitwiseQuery(selector, value, (result, mask) => result < mask);
export {
  $bitsAnyClear
};
