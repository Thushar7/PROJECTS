// Temporary ambient module declarations for dynamically imported libs
// If you install @types/html2canvas or jspdf types, you can remove this file.

declare module 'html2canvas' {
  interface Html2CanvasOptions {
    scale?: number;
    backgroundColor?: string | null;
  }
  function html2canvas(element: HTMLElement, options?: Html2CanvasOptions): Promise<HTMLCanvasElement>;
  export default html2canvas;
}

declare module 'jspdf' {
  interface JsPDFOptions { orientation?: string; unit?: string; format?: string | number[]; }
  class jsPDF {
    constructor(options?: JsPDFOptions);
    internal: any;
    addImage(imageData: string, format: string, x: number, y: number, width: number, height: number): void;
    save(filename: string): void;
  }
  export { jsPDF };
  export default jsPDF;
}
