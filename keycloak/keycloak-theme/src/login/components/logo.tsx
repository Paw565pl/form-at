interface LogoProps {
  className?: string;
}

export const Logo = ({ className }: LogoProps) => (
  <svg className={className} width="40" height="20" viewBox="0 0 25 12" fill="none" xmlns="http://www.w3.org/2000/svg">
    <g clipPath="url(#clip0_490_82)">
      <path
        d="M6 11C8.76142 11 11 8.76142 11 6C11 3.23858 8.76142 1 6 1C3.23858 1 1 3.23858 1 6C1 8.76142 3.23858 11 6 11Z"
        stroke="#1F7771"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path d="M8.5 3.5L3.5 8.5" stroke="#1F7771" strokeWidth="1.5" strokeLinejoin="round" />
      <path d="M3.5 3.5L8.5 8.5" stroke="#1F7771" strokeWidth="1.5" strokeLinejoin="round" />
    </g>
    <g clipPath="url(#clip1_490_82)">
      <path
        d="M16.5 11C19.2615 11 21.5 8.7615 21.5 6C21.5 3.2385 19.2615 1 16.5 1C13.7385 1 11.5 3.2385 11.5 6C11.5 8.7615 13.7385 11 16.5 11Z"
        stroke="#1F7771"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path d="M14 5.75L15.6667 7.5L19 4" stroke="#1F7771" strokeWidth="1.5" strokeLinejoin="round" />
    </g>
    <defs>
      <clipPath id="clip0_490_82">
        <rect width="12" height="12" fill="white" />
      </clipPath>
      <clipPath id="clip1_490_82">
        <rect width="12" height="12" fill="white" transform="translate(10.5)" />
      </clipPath>
    </defs>
  </svg>
);
