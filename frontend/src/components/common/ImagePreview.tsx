import React, { useState } from 'react';
import { Image as ImageIcon, X } from 'lucide-react';

interface ImagePreviewProps {
  url?: string;
  alt?: string;
  className?: string;
  onError?: () => void;
}

const ImagePreview: React.FC<ImagePreviewProps> = ({ 
  url, 
  alt = 'Preview', 
  className = 'w-full h-32',
  onError 
}) => {
  const [imageError, setImageError] = useState(false);

  const handleImageError = () => {
    setImageError(true);
    onError?.();
  };

  if (!url || imageError) {
    return (
      <div className={`bg-gray-100 border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center ${className}`}>
        <div className="text-center text-gray-500">
          <ImageIcon className="h-8 w-8 mx-auto mb-1" />
          <p className="text-sm">이미지 미리보기</p>
        </div>
      </div>
    );
  }

  return (
    <div className={`relative bg-gray-100 rounded-lg overflow-hidden ${className}`}>
      <img
        src={url}
        alt={alt}
        className="w-full h-full object-cover"
        onError={handleImageError}
      />
    </div>
  );
};

export default ImagePreview;
