import React, { useState, useEffect } from 'react';
import { Clock } from 'lucide-react';

interface CountdownTimerProps {
  endTime: string;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
  showIcon?: boolean;
  onExpire?: () => void;
}

export const CountdownTimer: React.FC<CountdownTimerProps> = ({
  endTime,
  className = '',
  size = 'md',
  showIcon = true,
  onExpire
}) => {
  const [timeRemaining, setTimeRemaining] = useState({
    days: 0,
    hours: 0,
    minutes: 0,
    seconds: 0,
    expired: false
  });

  const sizeClasses = {
    sm: 'text-xs',
    md: 'text-sm',
    lg: 'text-base'
  };

  useEffect(() => {
    const calculateTimeRemaining = () => {
      const end = new Date(endTime).getTime();
      const now = new Date().getTime();
      const difference = end - now;

      if (difference > 0) {
        const days = Math.floor(difference / (1000 * 60 * 60 * 24));
        const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((difference % (1000 * 60)) / 1000);

        setTimeRemaining({ days, hours, minutes, seconds, expired: false });
      } else {
        setTimeRemaining({ days: 0, hours: 0, minutes: 0, seconds: 0, expired: true });
        onExpire?.();
      }
    };

    calculateTimeRemaining();
    const interval = setInterval(calculateTimeRemaining, 1000);

    return () => clearInterval(interval);
  }, [endTime, onExpire]);

  if (timeRemaining.expired) {
    return (
      <div className={`flex items-center space-x-1 text-gray-500 ${sizeClasses[size]} ${className}`}>
        {showIcon && <Clock className="w-3 h-3" />}
        <span>Expired</span>
      </div>
    );
  }

  const formatTime = (value: number) => value.toString().padStart(2, '0');

  return (
    <div className={`flex items-center space-x-1 text-red-600 font-medium ${sizeClasses[size]} ${className}`}>
      {showIcon && <Clock className="w-3 h-3" />}
      <div className="flex items-center space-x-1">
        {timeRemaining.days > 0 && (
          <>
            <span className="bg-red-100 px-1.5 py-0.5 rounded text-xs font-mono">
              {formatTime(timeRemaining.days)}
            </span>
            <span className="text-xs">d</span>
          </>
        )}
        <span className="bg-red-100 px-1.5 py-0.5 rounded text-xs font-mono">
          {formatTime(timeRemaining.hours)}
        </span>
        <span className="text-xs">:</span>
        <span className="bg-red-100 px-1.5 py-0.5 rounded text-xs font-mono">
          {formatTime(timeRemaining.minutes)}
        </span>
        <span className="text-xs">:</span>
        <span className="bg-red-100 px-1.5 py-0.5 rounded text-xs font-mono">
          {formatTime(timeRemaining.seconds)}
        </span>
      </div>
    </div>
  );
};