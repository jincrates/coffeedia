import React, {forwardRef, InputHTMLAttributes} from 'react';
import {cn} from '@/utils/cn';

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
    ({
       className,
       type = 'text',
       label,
       error,
       helperText,
       leftIcon,
       rightIcon,
       id,
       ...props
     }, ref) => {
      const reactId = React.useId();
      const inputId = id || reactId;

      return (
          <div className="w-full">
            {label && (
                <label
                    htmlFor={inputId}
                    className="block text-sm font-medium text-gray-700 mb-1"
                >
                  {label}
                </label>
            )}
            <div className="relative">
              {leftIcon && (
                  <div className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                    {leftIcon}
                  </div>
              )}
              <input
                  type={type}
                  id={inputId}
                  className={cn(
                      'flex h-10 w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-coffee-500 focus:border-transparent disabled:cursor-not-allowed disabled:opacity-50',
                      leftIcon && 'pl-10',
                      rightIcon && 'pr-10',
                      error && 'border-red-500 focus:ring-red-500',
                      className
                  )}
                  ref={ref}
                  {...props}
              />
              {rightIcon && (
                  <div
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                    {rightIcon}
                  </div>
              )}
            </div>
            {error && (
                <p className="mt-1 text-sm text-red-600">{error}</p>
            )}
            {helperText && !error && (
                <p className="mt-1 text-sm text-gray-500">{helperText}</p>
            )}
          </div>
      );
    }
);

Input.displayName = 'Input';

export default Input;
