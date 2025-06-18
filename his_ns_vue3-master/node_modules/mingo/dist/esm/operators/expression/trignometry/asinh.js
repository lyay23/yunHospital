import { processOperator } from "./_internal";
const $asinh = (obj, expr, options) => processOperator(obj, expr, options, Math.asinh, {
  Infinity: Infinity,
  "-Infinity": -Infinity
});
export {
  $asinh
};
