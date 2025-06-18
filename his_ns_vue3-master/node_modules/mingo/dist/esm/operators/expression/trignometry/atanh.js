import { processOperator } from "./_internal";
const $atanh = (obj, expr, options) => processOperator(obj, expr, options, Math.atanh, {
  1: Infinity,
  "-1": -Infinity
});
export {
  $atanh
};
