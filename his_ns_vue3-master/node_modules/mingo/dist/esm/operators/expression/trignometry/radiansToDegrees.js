import { processOperator } from "./_internal";
const radiansToDegrees = (n) => n * (180 / Math.PI);
const $radiansToDegrees = (obj, expr, options) => processOperator(obj, expr, options, radiansToDegrees, {
  Infinity: Infinity,
  "-Infinity": Infinity
});
export {
  $radiansToDegrees
};
