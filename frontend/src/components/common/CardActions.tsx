import React, { useState } from 'react';
import { MoreVertical, Edit, Trash2, Eye, ExternalLink } from 'lucide-react';
import Button from '@/components/common/Button';

interface CardAction {
  type: 'view' | 'edit' | 'delete' | 'external' | 'custom';
  label: string;
  icon?: React.ReactNode;
  onClick: () => void;
  variant?: 'default' | 'destructive';
  disabled?: boolean;
}

interface CardActionsProps {
  actions: CardAction[];
  position?: 'dropdown' | 'buttons';
  size?: 'sm' | 'md';
}

const CardActions: React.FC<CardActionsProps> = ({
  actions,
  position = 'dropdown',
  size = 'sm',
}) => {
  const [showDropdown, setShowDropdown] = useState(false);

  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (showDropdown) {
        setShowDropdown(false);
      }
    };

    if (showDropdown) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [showDropdown]);

  const getDefaultIcon = (type: string) => {
    switch (type) {
      case 'view':
        return <Eye className="h-4 w-4" />;
      case 'edit':
        return <Edit className="h-4 w-4" />;
      case 'delete':
        return <Trash2 className="h-4 w-4" />;
      case 'external':
        return <ExternalLink className="h-4 w-4" />;
      default:
        return null;
    }
  };

  const handleActionClick = (action: CardAction, e: React.MouseEvent) => {
    e.stopPropagation();
    action.onClick();
    setShowDropdown(false);
  };

  const toggleDropdown = (e: React.MouseEvent) => {
    e.stopPropagation();
    setShowDropdown(!showDropdown);
  };

  if (position === 'buttons') {
    return (
      <div className="flex items-center gap-2">
        {actions.map((action, index) => (
          <Button
            key={index}
            variant={action.variant === 'destructive' ? 'destructive' : 'outline'}
            size={size}
            onClick={(e) => handleActionClick(action, e)}
            disabled={action.disabled}
            leftIcon={action.icon || getDefaultIcon(action.type)}
          >
            {action.label}
          </Button>
        ))}
      </div>
    );
  }

  return (
    <div className="relative">
      <Button
        variant="ghost"
        size={size}
        onClick={toggleDropdown}
        className="p-1 h-8 w-8"
      >
        <MoreVertical className="h-4 w-4" />
      </Button>
      
      {showDropdown && (
        <div className="absolute right-0 top-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10 min-w-[140px]">
          {actions.map((action, index) => (
            <button
              key={index}
              onClick={(e) => handleActionClick(action, e)}
              disabled={action.disabled}
              className={`w-full px-3 py-2 text-left text-sm hover:bg-gray-50 flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed ${
                action.variant === 'destructive'
                  ? 'text-red-600 hover:bg-red-50'
                  : 'text-gray-700'
              } ${index === 0 ? 'rounded-t-lg' : ''} ${
                index === actions.length - 1 ? 'rounded-b-lg' : ''
              }`}
            >
              {action.icon || getDefaultIcon(action.type)}
              {action.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default CardActions;
