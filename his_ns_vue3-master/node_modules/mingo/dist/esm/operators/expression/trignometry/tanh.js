import { processOperator } from "./_internal";
const $tanh = (obj, expr, options) => processOperator(obj, expr, options, Math.tanh, {
  Infinity: 1,
  "-Infinity": -1
});
export {
  $tanh
};
