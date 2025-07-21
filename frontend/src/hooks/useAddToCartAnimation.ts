import { useCallback } from 'react';

interface AnimationConfig {
  productImageUrl: string;
  productName: string;
  sourceElement: HTMLElement;
  targetElement?: HTMLElement;
  onComplete?: () => void;
}

export const useAddToCartAnimation = () => {
  const animateAddToCart = useCallback((config: AnimationConfig) => {
    const {
      productImageUrl,
      productName,
      sourceElement,
      targetElement,
      onComplete
    } = config;

    console.log('🚀 Starting animation with config:', {
      productImageUrl,
      productName,
      hasSourceElement: !!sourceElement,
      hasTargetElement: !!targetElement
    });

    // Find target element (cart icon) if not provided
    const target = targetElement || document.querySelector('[data-cart-icon]') as HTMLElement;
    if (!target) {
      console.error('❌ Cart target element not found - make sure cart icon has data-cart-icon attribute');
      onComplete?.();
      return;
    }

    console.log('✅ Target element found:', target);

    // Get source and target positions
    const sourceRect = sourceElement.getBoundingClientRect();
    const targetRect = target.getBoundingClientRect();

    console.log('📍 Position info:', {
      source: { x: sourceRect.x, y: sourceRect.y, width: sourceRect.width, height: sourceRect.height },
      target: { x: targetRect.x, y: targetRect.y, width: targetRect.width, height: targetRect.height }
    });

    // Create animated element
    const animatedElement = document.createElement('div');
    animatedElement.className = 'add-to-cart-animation';
    
    // Use fallback image if none provided
    const imageUrl = productImageUrl || 'https://via.placeholder.com/48/3B82F6/FFFFFF?text=📦';
    
    animatedElement.innerHTML = `
      <div class="relative w-12 h-12 rounded-lg overflow-hidden shadow-lg border-2 border-blue-500 bg-white">
        <img 
          src="${imageUrl}" 
          alt="${productName}"
          class="w-full h-full object-cover"
          onerror="this.parentElement.innerHTML='<div class=\\'w-full h-full bg-blue-100 flex items-center justify-center text-blue-600 text-lg\\'>📦</div>'"
        />
      </div>
    `;

    console.log('🎨 Created animated element:', animatedElement);

    // Set initial position and styles
    Object.assign(animatedElement.style, {
      position: 'fixed',
      top: `${sourceRect.top + sourceRect.height / 2 - 24}px`,
      left: `${sourceRect.left + sourceRect.width / 2 - 24}px`,
      width: '48px',
      height: '48px',
      zIndex: '9999',
      pointerEvents: 'none',
      transition: 'all 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94)',
      transform: 'scale(1)',
      opacity: '1'
    });

    document.body.appendChild(animatedElement);
    console.log('📍 Added element to DOM at position:', animatedElement.style.top, animatedElement.style.left);

    // Trigger animation on next frame
    requestAnimationFrame(() => {
      console.log('🎬 Starting animation frame');
      
      // Calculate target position (center of cart icon)
      const targetX = targetRect.left + targetRect.width / 2 - 24;
      const targetY = targetRect.top + targetRect.height / 2 - 24;

      console.log('🎯 Moving to target position:', { targetX, targetY });

      // Apply final animation styles
      Object.assign(animatedElement.style, {
        top: `${targetY}px`,
        left: `${targetX}px`,
        transform: 'scale(0.5)',
        opacity: '0.8'
      });

      // Add bounce animation to cart icon
      target.classList.add('cart-bounce');
      console.log('🎾 Added bounce animation to cart icon');
      
      // Clean up cart animation class
      setTimeout(() => {
        target.classList.remove('cart-bounce');
        console.log('✨ Removed bounce animation');
      }, 300);

      // Clean up after animation
      setTimeout(() => {
        if (document.body.contains(animatedElement)) {
          document.body.removeChild(animatedElement);
          console.log('🧹 Cleaned up animated element');
        }
        onComplete?.();
        console.log('✅ Animation complete!');
      }, 800);
    });
  }, []);

  return { animateAddToCart };
};