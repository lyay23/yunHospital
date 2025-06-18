import { processOperator } from "./_internal";
const degreesToRadians = (n) => n * (Math.PI / 180);
const $degreesToRadians = (obj, expr, options) => processOperator(obj, expr, options, degreesToRadians, {
  Infinity: Infinity,
  "-Infinity": Infinity
});
export {
  $degreesToRadians
};
