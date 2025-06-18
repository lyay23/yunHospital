import { MAX_LONG, MIN_LONG, toInteger } from "./_internal";
const $toLong = (obj, expr, options) => toInteger(obj, expr, options, MIN_LONG, MAX_LONG);
export {
  $toLong
};
