import { processOperator } from "./_internal";
const $sinh = (obj, expr, options) => processOperator(obj, expr, options, Math.sinh, {
  "-Infinity": -Infinity,
  Infinity: Infinity
});
export {
  $sinh
};
