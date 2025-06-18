import { MAX_INT, MIN_INT, toInteger } from "./_internal";
const $toInt = (obj, expr, options) => toInteger(obj, expr, options, MIN_INT, MAX_INT);
export {
  $toInt
};
